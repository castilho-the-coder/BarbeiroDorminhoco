import java.util.Random; 

public class Barbeiro extends Thread { 
    
    private final Barbearia barbearia; 
    private final Random rand = new Random(); // gerador de números aleatórios para simular duração do corte

    public Barbeiro(Barbearia barbearia) { 
        this.barbearia = barbearia; 
    }

    @Override
    public void run() { 
        try {
            while (true) { // loop infinito: o barbeiro fica aguardando clientes até encerrar
               
                Cliente cliente = barbearia.proximoClienteParaAtender(); 

                // se retornou null, a barbearia foi fechada e não há mais clientes
                if (cliente == null) { 
                    System.out.println("Barbeiro: Barbearia fechada e sem clientes. Vou para casa."); 
                    break; 
                }

                // só atende o cliente se a barbearia ainda estiver aberta
                if (!barbearia.isAberta()) {
                    System.out.println("Barbeiro: Barbearia fechou. Não atendo mais clientes.");
                    break;
                }

                // simula o tempo de corte 
                int tempoCorte = rand.nextInt(3000) + 1000; 
                System.out.println("Barbeiro: Cortando cabelo de " + cliente.getNome() + "..."); 
                Thread.sleep(tempoCorte); 

                barbearia.finalizarAtendimento(cliente); // notifica a barbearia que o cliente foi atendido
            }
        } catch (InterruptedException e) { // captura interrupção da thread
            System.out.println("Barbeiro foi interrompido e foi para casa."); 
            Thread.currentThread().interrupt(); 
        }
    }
}