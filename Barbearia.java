import java.util.LinkedList;
import java.util.Queue;

public class Barbearia {

    private final int numCadeiras;
    private final Queue<Cliente> filaDeEspera;
    private volatile boolean aberta = true;

    public Barbearia(int numCadeiras) {
        this.numCadeiras = numCadeiras;
        this.filaDeEspera = new LinkedList<>();
    }

   
    public Cliente proximoClienteParaAtender() throws InterruptedException {
        Cliente proximoCliente;

        synchronized (this) {
            // Se não há clientes, o barbeiro dorme enquanto a barbearia estiver aberta.
            while (filaDeEspera.isEmpty() && aberta) {
                System.out.println("Barbeiro: Zzzzz... (sem clientes, vou dormir)");
                // Libera o lock e espera (wait) ser acordado
                this.wait();
            }

    
            if (filaDeEspera.isEmpty() && !aberta) {
                return null;
            }

            // clientes na fila: pega o próximo
            proximoCliente = filaDeEspera.poll();
            System.out.println("Barbeiro: Chamando " + proximoCliente.getNome() + ". (" + 
                               filaDeEspera.size() + " na espera)");
            
            // notifica threads (clientes) que podem estar esperando por uma vaga na fila
            this.notifyAll();
        }
        
        
        return proximoCliente;
    }

    
    public void clienteQuerCortar(Cliente cliente) throws InterruptedException {
        synchronized (this) {
            // Se a barbearia está fechada, o cliente vai embora
            if (!aberta) {
                System.out.println(cliente.getNome() + ": Barbearia fechada! Vou embora.");
                return;
            }
            // Se a barbearia está cheia, o cliente vai embora.
            if (filaDeEspera.size() == numCadeiras) {
                System.out.println(cliente.getNome() + ": Barbearia cheia! Vou embora.");
                return;
            }

            // cliente senta na cadeira de espera.
            filaDeEspera.add(cliente);
            System.out.println(cliente.getNome() + ": Sentei para esperar. (" + 
                               filaDeEspera.size() + " na espera)");

            // Avisa o barbeiro que há cliente disponível
            this.notifyAll();

            // O cliente espera até ser atendido ou até a barbearia fechar
            while (!cliente.isAtendido() && aberta) {
               
                this.wait();
            }

            if (cliente.isAtendido()) {
                System.out.println(cliente.getNome() + ": Cabelo cortado! Indo embora.");
            } else {
                System.out.println(cliente.getNome() + ": Saindo sem atendimento (barbearia fechada).");
            }
        }
    }

    
    public synchronized void fecharBarbearia() {
        aberta = false;
        System.out.println("Barbearia: Fechando as portas. Não aceitamos mais clientes.");
        this.notifyAll();
    }

    public boolean isAberta() {
        return aberta;
    }

  
    public synchronized void finalizarAtendimento(Cliente cliente) {
        cliente.setAtendido(true);
        System.out.println("Barbeiro: Terminei o corte de " + cliente.getNome());
        
        // Acorda o cliente específico que estava em wait()
        this.notifyAll();
    }
}