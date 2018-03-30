package pack.com.seventhsite.seventhsite;

/**
 * Created by Samsung_PC on 7/29/14.
 */
public class review {
    String header,isi,user;

    public review(String header, String isi, String user) {
        this.header = header;
        this.isi = isi;
        this.user = user;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getIsi() {
        return isi;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
