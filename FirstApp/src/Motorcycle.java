import java.util.Objects;

public class Motorcycle extends Vehicle {
    private String kategoria;

    public Motorcycle(int id, String brand, String model, int year, double price, boolean rented, String kategoria) {
        super(id, brand, model, year, price, rented);
        this.kategoria = kategoria;
    }

    public String getKategoria() {
        return kategoria;
    }

    @Override
    public String toCSV() {
        return super.toCSV() + ";" + kategoria;
    }

    @Override
    public String toString() {
        String subString = super.toString().substring(0, super.toString().length() - 1);
        return "Motorcycle" + subString + ", category=" + kategoria + "}";
    }

    @Override
    public Motorcycle clone() {
        Motorcycle motorcycle = new Motorcycle(this.getId(), this.getBrand(), this.getModel(), this.getYear(), this.getPrice(), this.isRented(), this.getKategoria());
        return motorcycle;
    }

    @Override
    public boolean equals(Object o)  {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Motorcycle vehicle = (Motorcycle) o;
        return this.getId() == vehicle.getId() && this.getYear() == vehicle.getYear() && Double.compare(vehicle.getPrice(), this.getPrice()) == 0 && this.isRented() == vehicle.isRented() && Objects.equals(this.getBrand(), vehicle.getBrand()) && Objects.equals(this.getModel(), vehicle.getModel()) && this.getKategoria() == vehicle.getKategoria();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getBrand(), this.getModel(), this.getYear(), this.getPrice(), this.isRented(), this.getKategoria());
    }
}
