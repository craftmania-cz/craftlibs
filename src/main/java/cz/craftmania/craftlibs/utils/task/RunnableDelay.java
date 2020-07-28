package cz.craftmania.craftlibs.utils.task;


public class RunnableDelay {

    private static int[] delays = new int[] {
            0, 10, 5, 15,
            2, 12, 7, 17,
            4, 14, 9, 19,
            1, 11, 6, 16,
            3, 13, 8, 18
    };

    private static int delaysCounter = 0;

    public static int getDelay() {
        return delays[(delaysCounter++) % delays.length];
    }
}

