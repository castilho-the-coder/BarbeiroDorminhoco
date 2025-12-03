import java.util.LinkedList; // LinkedList para implementar a fila de espera
import java.util.Queue; 

public class Barbearia { 

    private final int numCadeiras; // número de cadeiras de espera disponíveis
    private final Queue<Cliente> filaDeEspera; // fila de clientes esperando atendimento
    private volatile boolean aberta = true; // flag indicando se a barbearia está aberta

    public Barbearia(int numCadeiras) { 
        this.numCadeiras = numCadeiras; // armazena o número de cadeiras
        this.filaDeEspera = new LinkedList<>(); // inicializa a fila de espera
    }

   
    public Cliente proximoClienteParaAtender() throws InterruptedException { 
        Cliente proximoCliente; // variável que guarda o cliente a ser atendido

        synchronized (this) { 
            // barbeiro dorme enquanto não há clientes e a barbearia está aberta
            while (filaDeEspera.isEmpty() && aberta) { 
                System.out.println("Barbeiro: Zzzzz... (sem clientes, vou dormir)"); 
                this.wait(); // aguarda notificação de novas entradas ou fechamento
            }

    
            if (filaDeEspera.isEmpty() && !aberta) { // se não há clientes e a barbearia foi fechada
                return null; // retorna null indicando que não há mais serviço
            }

            // se há clientes na fila pega o próximo
            proximoCliente = filaDeEspera.poll(); // remove e obtém o próximo cliente da fila
            System.out.println("Barbeiro: Chamando " + proximoCliente.getNome() + ". (" +   filaDeEspera.size() + " na espera)"); 
            this.notifyAll(); 
        }
        
        
        return proximoCliente; // retorna o cliente a ser atendido
    }

    
    public void clienteQuerCortar(Cliente cliente) throws InterruptedException { // método chamado por um cliente que tenta cortar o cabelo
        synchronized (this) { // proteção para checar/alterar fila e estado
            // se a barbearia está fechada o cliente vai embora
            if (!aberta) { // verifica se a barbearia está fechada
                System.out.println(cliente.getNome() + ": Barbearia fechada! Vou embora."); // log de saída sem atendimento
                return; // cliente sai sem entrar na fila
            }
            // se a barbearia está cheia o cliente vai embora.
            if (filaDeEspera.size() == numCadeiras) { // verifica se a fila atingiu a capacidade
                System.out.println(cliente.getNome() + ": Barbearia cheia! Vou embora."); // log quando não há vaga
                return; // cliente vai embora sem sentar
            }

            // cliente senta na cadeira de espera.
            filaDeEspera.add(cliente); // adiciona o cliente à fila de espera
            System.out.println(cliente.getNome() + ": Sentei para esperar. (" + 
                               filaDeEspera.size() + " na espera)"); // informa que o cliente está esperando

            // avisa o barbeiro que há cliente disponível
            this.notifyAll(); // notifica possíveis barbeiro(s) adormecidos

            // cliente espera até ser atendido ou até a barbearia fechar
            while (!cliente.isAtendido() && aberta) { // enquanto o cliente não for atendido e a barbearia estiver aberta
               
                this.wait(); // aguarda notificação de atendimento ou fechamento
            }

            if (cliente.isAtendido()) { // se o cliente foi atendido
                System.out.println(cliente.getNome() + ": Cabelo cortado! Indo embora."); // log de satisfação
            } else { // senão, foi acordado por fechamento
                System.out.println(cliente.getNome() + ": Saindo sem atendimento (barbearia fechada)."); // log de saída sem atendimento
            }
        }
    }

    
    public synchronized void fecharBarbearia() { // método sincronizado para fechar a barbearia
        aberta = false; // marca a barbearia como fechada
        System.out.println("Barbearia: Fechando as portas. Não aceitamos mais clientes."); // log de fechamento
        this.notifyAll(); // notifica todas as threads para que saiam do wait
    }

    public boolean isAberta() { // consulta se a barbearia está aberta
        return aberta; // retorna o estado
    }

  
    public synchronized void finalizarAtendimento(Cliente cliente) { // método chamado pelo barbeiro ao terminar um corte
        cliente.setAtendido(true); // marca o cliente como atendido
        System.out.println("Barbeiro: Terminei o corte de " + cliente.getNome()); // log de conclusão do corte
        
        // acorda o cliente específico que estava em wait
        this.notifyAll(); // notifica para que o cliente acorde e prossiga
    }
}