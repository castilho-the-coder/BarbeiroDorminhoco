import java.util.Random; 
import java.util.ArrayList; 
import java.util.List; 

public class Simulacao { 

    public static void main(String[] args) { 
        int N_CADEIRAS = 3; // define quantas cadeiras de espera existem
        
        Barbearia barbearia = new Barbearia(N_CADEIRAS); // cria a barbearia com N cadeiras
        
        Barbeiro barbeiro = new Barbeiro(barbearia); // cria o barbeiro associado à barbearia
        barbeiro.start(); 

        int clienteId = 1; 
        Random rand = new Random(); 

        System.out.println("--- Barbearia aberta (N=" + N_CADEIRAS + ") ---"); 
        
        // gera clientes por um tempo limitado e depois fechar a barbearia
        int DURACAO_MS = 20_000; 
        long fim = System.currentTimeMillis() + DURACAO_MS; 

        List<Cliente> clientes = new ArrayList<>(); // lista para armazenar referências aos clientes criados

        while (System.currentTimeMillis() < fim) { 
            try {
                Cliente cliente = new Cliente(clienteId++, barbearia); // cria cliente com id crescente
                clientes.add(cliente); 
                cliente.start();
                int intervalo = rand.nextInt(1500) + 100; 
                Thread.sleep(intervalo); // aguarda antes de criar o próximo cliente

            } catch (InterruptedException e) { 
                e.printStackTrace(); 
                Thread.currentThread().interrupt(); 
                break; 
            }
        }

        System.out.println("--- Tempo esgotado: fechando a barbearia ---"); 
        barbearia.fecharBarbearia(); // sinaliza encerramento da barbearia

        // aguarda clientes terminarem (com timeout para não travar indefinidamente)
        for (Cliente c : clientes) { 
            try {
                c.join(1000); 
            } catch (InterruptedException e) { 
                Thread.currentThread().interrupt(); 
            }
        }

        // aguarda o barbeiro terminar
        try {
            barbeiro.join(); 
        } catch (InterruptedException e) { 
            Thread.currentThread().interrupt(); 
        }

        System.out.println("--- Simulação encerrada ---"); 
    }
}