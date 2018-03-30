package pack.com.seventhsite.seventhsite;

/**
 * Created by Samsung_PC on 8/5/2014.
 */
public class transaksi {
    String OrderId,OrderTotal,OrderDate,NoResi;
    int StatusId,Ongkir;

    public transaksi(String orderId, String orderTotal, String orderDate, int statusId,int ongkir,String noresi) {
        OrderId = orderId;
        OrderTotal = orderTotal;
        OrderDate = orderDate;
        StatusId = statusId;
        Ongkir = ongkir;
        NoResi=noresi;
    }

    public String getNoResi() {
        return NoResi;
    }

    public void setNoResi(String noResi) {
        NoResi = noResi;
    }

    public int getOngkir() {
        return Ongkir;
    }

    public void setOngkir(int ongkir) {
        Ongkir = ongkir;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getOrderTotal() {
        return OrderTotal;
    }

    public void setOrderTotal(String orderTotal) {
        OrderTotal = orderTotal;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public int getStatusId() {
        return StatusId;
    }

    public void setStatusId(int statusId) {
        StatusId = statusId;
    }
}
