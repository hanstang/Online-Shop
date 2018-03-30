package pack.com.seventhsite.seventhsite;

/**
 * Created by Samsung_PC on 7/25/14.
 */
public class Product {
    public int idbarang;
    public String namaBarang;
    public String gambar;
    public int harga;
    public int jumlah;
    public double diskon;

    public Product(int idbarang,String namaBarang, String gambar, int harga, int jumlah, double diskon) {
        this.idbarang = idbarang;
        this.namaBarang = namaBarang;
        this.gambar = gambar;
        this.harga = harga;
        this.jumlah = jumlah;
        this.diskon = diskon;
    }

    public int getIdbarang() {
        return idbarang;
    }

    public double getDiskon() {
        return diskon;
    }

    public void setDiskon(int diskon) {
        this.diskon = diskon;
    }

    public void setIdbarang(int idbarang) {
        this.idbarang = idbarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }
}
