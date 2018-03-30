package pack.com.seventhsite.seventhsite;

/**
 * Created by Samsung_PC on 8/1/2014.
 */
public class kategori {
    private String namaKategori;
    int catId;
    boolean isRoot=true;

    public kategori(boolean isRoot, int catId, String namaKategori) {
        this.isRoot = isRoot;
        this.catId = catId;
        this.namaKategori = namaKategori;
    }

    public kategori(int catId, String namaKategori) {
        this.catId = catId;
        this.namaKategori = namaKategori;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    public String getNamaKategori() {
        return namaKategori;
    }

    public void setNamaKategori(String namaKategori) {
        this.namaKategori = namaKategori;
    }
}
