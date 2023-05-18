public class Server {
    private String type;
    private int id;
    private int state;
    private int cores;
    private int memory;
    private int disk;

    public Server(String type, int id, int state, int cores, int memory, int disk) {
        this.type = type;
        this.id = id;
        this.state = state;
        this.cores = cores;
        this.memory = memory;
        this.disk = disk;
    }

    public String getType() {
        return type;
    }

    public int getID() {
        return id;
    }

    public boolean hasSufficientResourcesFor(Job job) {
        return job.getCore() <= cores && job.getMemory() <= memory && job.getDisk() <= disk;
    }

    public boolean isActive() {
        return state == 2 || state == 3;
    }

    public boolean isBooting() {
        return state == 1;
    }
}