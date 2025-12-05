public class Cliente extends Thread { 

    private final int id; // identificador do cliente
    private final Barbearia barbearia; // referência à barbearia que o cliente vai visitar
    private volatile boolean atendido = false; // flag indicando se o cliente já foi atendido

    public Cliente(int id, Barbearia barbearia) { 
        this.id = id; 
        this.barbearia = barbearia; 
    }

    @Override
    public void run() { 
        try {
            barbearia.clienteQuerCortar(this); // chama o método que lida com a lógica de espera/atendimento
            
        } catch (InterruptedException e) { // captura interrupção enquanto espera
            System.out.println("Cliente " + id + " foi interrompido."); 
            Thread.currentThread().interrupt(); 
        }
    }

    public String getNome() { 
        return "Cliente " + id; 
    }

    public boolean isAtendido() { // consulta se o cliente foi atendido
        return atendido; // retorna o estado
    }

    public void setAtendido(boolean atendido) { // marca o cliente como atendido
        this.atendido = atendido; // define a flag atendido
    }
}