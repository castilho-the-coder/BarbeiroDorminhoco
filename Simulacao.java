import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class Simulacao {

    public static void main(String[] args) {
        // Define o número de cadeiras de espera na barbearia
        int N_CADEIRAS = 3;
        
        Barbearia barbearia = new Barbearia(N_CADEIRAS);
        
        // Cria e inicia a thread do Barbeiro
        Barbeiro barbeiro = new Barbeiro(barbearia);
        barbeiro.start();

        // Gerador de clientes
        int clienteId = 1;
        Random rand = new Random();

        System.out.println("--- Barbearia aberta (N=" + N_CADEIRAS + ") ---");
        
        // Gerar clientes por um tempo limitado e depois fechar a barbearia
        int DURACAO_MS = 20_000; // duração da simulação (ms)
        long fim = System.currentTimeMillis() + DURACAO_MS;

        List<Cliente> clientes = new ArrayList<>();

        while (System.currentTimeMillis() < fim) {
            try {
                // Cria e inicia uma nova thread de Cliente
                Cliente cliente = new Cliente(clienteId++, barbearia);
                clientes.add(cliente);
                cliente.start();

                // Aguarda um tempo aleatório antes de gerar o próximo cliente
                int intervalo = rand.nextInt(1500) + 100; // 0.1 a 1.6 segundos
                Thread.sleep(intervalo);

            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("--- Tempo esgotado: fechando a barbearia ---");
        // Fecha a barbearia para novos clientes e acorda threads
        barbearia.fecharBarbearia();

        // Aguarda clientes terminarem (com timeout para não travar indefinidamente)
        for (Cliente c : clientes) {
            try {
                c.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Aguarda o barbeiro terminar
        try {
            barbeiro.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("--- Simulação encerrada ---");
    }
}