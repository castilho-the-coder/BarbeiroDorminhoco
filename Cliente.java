public class Cliente extends Thread {

    private final int id;
    private final Barbearia barbearia;
    // volatile garante que a mudança feita pelo Barbeiro seja visível para a thread do Cliente
    private volatile boolean atendido = false;

    public Cliente(int id, Barbearia barbearia) {
        this.id = id;
        this.barbearia = barbearia;
    }

    @Override
    public void run() {
        try {
            // O cliente tenta entrar na barbearia para cortar o cabelo.
            // Este método bloqueia a thread do cliente até o atendimento terminar.
            barbearia.clienteQuerCortar(this);
            
        } catch (InterruptedException e) {
            System.out.println("Cliente " + id + " foi interrompido.");
            Thread.currentThread().interrupt();
        }
    }

    public String getNome() {
        return "Cliente " + id;
    }

    public boolean isAtendido() {
        return atendido;
    }

    public void setAtendido(boolean atendido) {
        this.atendido = atendido;
    }
}