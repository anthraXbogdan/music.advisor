public class Viewer extends Main{

    public static void playlistViewer() {

        for (int i = entryNo; i < rightBound; i++) {
            System.out.println(viewer.get(i));
        }
        System.out.println();
        System.out.println("---PAGE " + page + " OF " + totalPages2 + "---");

    }

    public static void nextPage() {

        entryNo += entriesPerPage;
        rightBound += entriesPerPage;
        if (entryNo < viewer.size()) {
            page++;
            if (rightBound < viewer.size()) {
                for (int i = entryNo; i < rightBound; i++) {
                    System.out.println(viewer.get(i));
                }
                System.out.println();
                System.out.println("---PAGE " + page + " OF " + totalPages2 + "---");
            } else {
                for (int i = entryNo; i < viewer.size(); i++) {
                    System.out.println(viewer.get(i));
                }
                System.out.println();
                System.out.println("---PAGE " + page + " OF " + totalPages2 + "---");
            }
        } else {
            System.out.println("No more pages.");
            entryNo -= entriesPerPage;
            rightBound -= entriesPerPage;
        }

    }

    public static void prevPage() {

        entryNo -= entriesPerPage;
        rightBound -= entriesPerPage;
        page--;
        if (entryNo >= 0) {
            for (int i = entryNo; i < rightBound; i++) {
                System.out.println(viewer.get(i));
            }
            System.out.println();
            System.out.println("---PAGE " + page + " OF " + totalPages2 + "---");
        } else {
            System.out.println("No more pages.");
            entryNo = 0;
            rightBound = entriesPerPage;
            page = 1;
        }

    }

}
