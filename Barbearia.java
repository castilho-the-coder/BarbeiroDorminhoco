import java.util.LinkedList;
import java.util.Queue;

/**
 * A classe Barbearia atua como o MONITOR.
 * Ela gerencia o estado compartilhado (a fila de espera) e
 * sincroniza as threads do barbeiro e dos clientes usando
 * synchronized, wait() e notifyAll().
 */
public class Barbearia {

    private final int numCadeiras;
    private final Queue<Cliente> filaDeEspera;
    // indica se a barbearia está aberta para novos clientes
    private volatile boolean aberta = true;

    public Barbearia(int numCadeiras) {
        this.numCadeiras = numCadeiras;
        this.filaDeEspera = new LinkedList<>();
    }

    /**
     * Método chamado pela thread do Barbeiro.
     * O barbeiro tenta pegar um cliente para atender.
     */
    public Cliente proximoClienteParaAtender() throws InterruptedException {
        Cliente proximoCliente;

        synchronized (this) {
            // 1. Se não há clientes, o barbeiro dorme enquanto a barbearia estiver aberta.
            while (filaDeEspera.isEmpty() && aberta) {
                System.out.println("Barbeiro: Zzzzz... (sem clientes, vou dormir)");
                // Libera o lock e espera (wait) ser acordado
                this.wait();
            }

            // Se não há clientes e a barbearia foi fechada, sinaliza término retornando null
            if (filaDeEspera.isEmpty() && !aberta) {
                return null;
            }

            // 2. Há clientes na fila: pega o próximo
            proximoCliente = filaDeEspera.poll();
            System.out.println("Barbeiro: Chamando " + proximoCliente.getNome() + ". (" + 
                               filaDeEspera.size() + " na espera)");
            
            // Notifica threads (clientes) que podem estar esperando por uma vaga na fila.
            this.notifyAll();
        }
        
        // Retorna o cliente para que o "corte" (sleep) ocorra fora do monitor
        return proximoCliente;
    }

    /**
     * Método chamado pela thread do Cliente.
     * O cliente tenta entrar na barbearia e esperar.
     */
    public void clienteQuerCortar(Cliente cliente) throws InterruptedException {
        synchronized (this) {
            // Se a barbearia está fechada, o cliente vai embora
            if (!aberta) {
                System.out.println(cliente.getNome() + ": Barbearia fechada! Vou embora.");
                return;
            }
            // 1. Se a barbearia está cheia, o cliente vai embora.
            if (filaDeEspera.size() == numCadeiras) {
                System.out.println(cliente.getNome() + ": Barbearia cheia! Vou embora.");
                return;
            }

            // 2. Há espaço. O cliente senta na cadeira de espera.
            filaDeEspera.add(cliente);
            System.out.println(cliente.getNome() + ": Sentei para esperar. (" + 
                               filaDeEspera.size() + " na espera)");

            // Avisa o barbeiro (ou qualquer thread interessada) que há cliente disponível
            this.notifyAll();

            // 4. O cliente espera (wait) até ser atendido ou até a barbearia fechar
            // Ele só sai do 'wait' quando o barbeiro chamar finalizarAtendimento()
            while (!cliente.isAtendido() && aberta) {
                // Libera o lock e espera ser notificado pelo barbeiro
                this.wait();
            }

            if (cliente.isAtendido()) {
                System.out.println(cliente.getNome() + ": Cabelo cortado! Indo embora.");
            } else {
                System.out.println(cliente.getNome() + ": Saindo sem atendimento (barbearia fechada).");
            }
        }
    }

    /**
     * Fecha a barbearia: impede novos clientes e acorda threads para terminar
     */
    public synchronized void fecharBarbearia() {
        aberta = false;
        System.out.println("Barbearia: Fechando as portas. Não aceitamos mais clientes.");
        this.notifyAll();
    }

    public boolean isAberta() {
        return aberta;
    }

    /**
     * Método chamado pela thread do Barbeiro após o corte.
     * Notifica o cliente que seu atendimento terminou.
     */
    public synchronized void finalizarAtendimento(Cliente cliente) {
        cliente.setAtendido(true);
        System.out.println("Barbeiro: Terminei o corte de " + cliente.getNome());
        
        // Acorda o cliente específico que estava em wait()
        // Usamos notifyAll() para garantir que a thread correta acorde
        this.notifyAll();
    }
}