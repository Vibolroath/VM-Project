public class Job {
    private int id;
    private int core;
    private int memory;
    private int disk;

    public Job(int id, int core, int memory, int disk) {
        this.id = id;
        this.core = core;
        this.memory = memory;
        this.disk = disk;
    }

    public int getID() {
        return id;
    }

    public int getCore() {
        return core;
    }

    public int getMemory() {
        return memory;
    }

    public int getDisk() {
        return disk;
    }
}