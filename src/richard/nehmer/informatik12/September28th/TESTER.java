package richard.nehmer.informatik12.September28th;

public class TESTER {

    // Attribute
    private List l;

    // Methoden
    public void Testen() {
        // Anlegen einer leeren Liste
        System.out.println("Leere Liste anlegen:");
        l = new List(8);
        l.printToLine();
        System.out.println();

        // Hinzufügen von 9 Elementen
        System.out.println("Neun äußerst wichtige Physiker hinzufügen:");
        l.addToList(new Physichists("Michael", "Prautzsch", 1971, -1));
        l.addToList(new Physichists("Pierre", "Curie", 1859, 1906));
        l.addToList(new Physichists("Michael", "Prautzsch2", 1971, -1));
        l.addToList(new Physichists("Hendrik Antoon", "Lorentz", 1853, 1928));
        l.addToList(new Physichists("Ludwig", "Boltzmann", 1844, 1906));
        l.addToList(new Physichists("Nikola", "Tesla", 1856, 1943));
        l.addToList(new Physichists("Albert", "Einstein", 1879, 1955));
        l.addToList(new Physichists("Michael", "Prautzsch3", 1971, -1));
        l.addToList(new Physichists("Michael", "Prautzsch4", 1971, -1));

        l.printToLine();
        System.out.println("Hier stehen nur acht! Warum wohl?");
        System.out.println();

        // Entfernen des letzten, mittleren und ersten Elements aus der Liste
        System.out.println("Da hat sich doch ein unwürdiger Wicht eingeschlichen!");
        System.out.println("Entfernt ihn!");
        System.out.println("Den ersten und letzten entfernen wir über den Namen,");
        System.out.println("den mittleren über seinen Listenplatz.");

        // Remove by object (first "Michael Prautzsch")
        l.removeFromList(new Physichists("Michael", "Prautzsch", -1, -1));
        // Remove by index (middle: index 2 in the current list)
        l.removeFromList(2);
        // Remove by object (last "Michael Prautzsch3")
        l.removeFromList(new Physichists("Michael", "Prautzsch3", -1, -1));

        l.printToLine();
        System.out.println();

        // Wiederauffüllen mit weiteren 4 Physikern
        System.out.println("Vier weitere Physiker hinzufügen:");
        l.addToList(new Physichists("Michael", "Faraday", 1791, 1867));
        l.addToList(new Physichists("Alessandro", "Volta", 1745, 1827));
        l.addToList(new Physichists("André-Marie", "Ampère", 1775, 1836));
        l.addToList(new Physichists("Michael", "Prautzsch", 1971, -1));

        l.printToLine();
        System.out.println("Diesmal hat es nur mit drei von ihnen geklappt.");
        System.out.println();
    }

    // Optional main to run quickly:
    public static void main(String[] args) {
        new TESTER().Testen();
    }
}