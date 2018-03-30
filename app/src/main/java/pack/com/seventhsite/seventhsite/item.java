package pack.com.seventhsite.seventhsite;

/**
 * Created by Samsung_PC on 8/1/2014.
 */
public class item {
    private String namaItem;
    private int hargaItem;
    private String gambar;
    private double diskon;
    private int id;
    private int rating;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public item(String namaItem, int hargaItem, String gambar, int id, double diskon, int rating) {

        this.namaItem = namaItem;
        this.hargaItem = hargaItem;
        this.gambar = gambar;
        this.id = id;
        this.diskon = diskon;
        this.rating = rating;
    }

    public void setDiskon(double diskon) {
        this.diskon = diskon;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public double getDiskon() {
        return diskon;
    }

    public void setDiskon(int diskon) {
        this.diskon = diskon;
    }

    public String getNamaItem() {
        return namaItem;
    }

    public void setNamaItem(String namaItem) {
        this.namaItem = namaItem;
    }

    public int getHargaItem() {
        return hargaItem;
    }

    public void setHargaItem(int hargaItem) {
        this.hargaItem = hargaItem;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public item(String namaItem, int hargaItem, String gambar) {

        this.namaItem = namaItem;
        this.hargaItem = hargaItem;
        this.gambar = gambar;
    }
}
