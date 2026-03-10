package aulas;

public class Aula2  implements Runnable{

    int id;

    public Aula2(int id){
        this.id = id;
    }

    public void run() {
        for (int i = 1; i <= 10 ; i++) {
            System.out.println(id);
        }
    }

    public static void main(String[] args) {
        Thread[] as = new Thread[5];
        as[0] = Thread.ofPlatform().start(new Aula2(1));
        as[0] = Thread.ofVirtual().start(new Aula2(2));
        //forma feia = fazer um thread.sleep
        //forma certa:
        for (Thread a : as) {
            try {
                a.join(); //"vou ficar parado te esperando"
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}