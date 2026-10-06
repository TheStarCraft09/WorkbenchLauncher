package richard.nehmer.informatik12.September28th;

public class Physichists {
    private String surname;
    private String firstName;
    private int birthYear;
    private int deathYear;

    public Physichists(String firstName, String surname, int birthYear, int deathYear) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (surname == null || surname.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        if (birthYear < 0) {
            throw new IllegalArgumentException("Birth year cannot be negative");
        }
        if (deathYear != -1 && deathYear < birthYear) {
            throw new IllegalArgumentException("Death year cannot be earlier than the birth year");
        }

        this.firstName = firstName;
        this.surname = surname;
        this.birthYear = birthYear;
        this.deathYear = deathYear;
    }

    public String returnInfo() {
        return surname + " " + firstName;
    }

    @Override
    public String toString() {
        if (deathYear == -1) {
            return returnInfo() + " (*" + birthYear + ")";
        }
        return returnInfo() + " (*" + birthYear + ", †" + deathYear + ")";
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Physichists)) {
            return false;
        }
        Physichists other = (Physichists) object;
        return surname.equals(other.surname) && firstName.equals(other.firstName);
    }

    @Override
    public int hashCode() {
        return surname.hashCode() + firstName.hashCode();
    }
}