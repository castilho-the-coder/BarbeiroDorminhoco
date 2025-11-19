import java.util.Random;

public class Barbeiro extends Thread {
    
    private final Barbearia barbearia;
    private final Random rand = new Random();

    public Barbeiro(Barbearia barbearia) {
        this.barbearia = barbearia;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // próximo cliente
                Cliente cliente = barbearia.proximoClienteParaAtender();

                // se retornou null, a barbearia foi fechada e não há mais clientes
                if (cliente == null) {
                    System.out.println("Barbeiro: Barbearia fechada e sem clientes. Vou para casa.");
                    break;
                }

                // simula o tempo de corte 
                int tempoCorte = rand.nextInt(3000) + 1000; // 1-4 segundos
                System.out.println("Barbeiro: Cortando cabelo de " + cliente.getNome() + "...");
                Thread.sleep(tempoCorte);

                // sinaliza o fim do corte 
                barbearia.finalizarAtendimento(cliente);
            }
        } catch (InterruptedException e) {
            System.out.println("Barbeiro foi interrompido e foi para casa.");
            Thread.currentThread().interrupt();
        }
    }
}