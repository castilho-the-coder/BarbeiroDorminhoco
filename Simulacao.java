import java.util.Random; 
import java.util.ArrayList; 
import java.util.List; 

public class Simulacao { // classe que contém o método main para executar a simulação

    public static void main(String[] args) { 
        int N_CADEIRAS = 3; // define quantas cadeiras de espera existem
        
        Barbearia barbearia = new Barbearia(N_CADEIRAS); // cria a barbearia com N cadeiras
        
        Barbeiro barbeiro = new Barbeiro(barbearia); // cria o barbeiro associado à barbearia
        barbeiro.start(); // inicia a thread do barbeiro

        int clienteId = 1; // contador para ids dos clientes
        Random rand = new Random(); // gerador de números aleatórios para intervalos entre clientes

        System.out.println("--- Barbearia aberta (N=" + N_CADEIRAS + ") ---"); 
        
        // gera clientes por um tempo limitado e depois fechar a barbearia
        int DURACAO_MS = 20_000; // duração total da geração de clientes em milissegundos
        long fim = System.currentTimeMillis() + DURACAO_MS; 

        List<Cliente> clientes = new ArrayList<>(); // lista para armazenar referências aos clientes criados

        while (System.currentTimeMillis() < fim) { 
            try {
                Cliente cliente = new Cliente(clienteId++, barbearia); // cria cliente com id crescente
                clientes.add(cliente); // guarda referência para depois aguardar término
                cliente.start(); // inicia a thread do cliente

                int intervalo = rand.nextInt(1500) + 100; // intervalo entre 100 e 1599 ms (~0.1 a 1.5s)
                Thread.sleep(intervalo); // aguarda antes de criar o próximo cliente

            } catch (InterruptedException e) { // se a thread principal for interrompida
                e.printStackTrace(); 
                Thread.currentThread().interrupt(); 
                break; // sai do loop de geração
            }
        }

        System.out.println("--- Tempo esgotado: fechando a barbearia ---"); 
        barbearia.fecharBarbearia(); // sinaliza encerramento da barbearia

        // aguarda clientes terminarem (com timeout para não travar indefinidamente)
        for (Cliente c : clientes) { 
            try {
                c.join(1000); // aguarda até 1s pela finalização de cada cliente
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