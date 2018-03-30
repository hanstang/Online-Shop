package pack.com.seventhsite.seventhsite;

import java.util.ArrayList;

/**
 * Created by Samsung_PC on 7/25/14.
 */
public class ShoppingCartHelper {
    public static final ArrayList<Product> cart=new ArrayList<Product>();
    static int jumlahbarang=0;

    public ShoppingCartHelper() {
    }

    public static void addBarang(int idbarang,String namabarang,String gambar,int harga,double diskon) {
        //cek dulu kembar atau tidak
        boolean kembar=false;
        if(cart.isEmpty()==false)
        {
            for (int i=0;i<cart.size();i++)
            {
                if(cart.get(i).getIdbarang()==idbarang)
                {
                    kembar=true;
                    cart.get(i).setJumlah(cart.get(i).getJumlah()+1);
                }
            }
        }
        if(kembar==false)
        {
            cart.add(new Product(idbarang,namabarang,gambar,harga,1,diskon));
        }
        jumlahbarang++;
    }

    public static void removeBarang(int index)
    {
        cart.remove(index);
    }

    public static ArrayList<Product> getCart() {
        return cart;
    }

    public static  void clearCart()
    {
        cart.clear();
    }

    public static int getjumlahbarang() {
        //return jumlahbarang;
        //return cart.size();
        int jumlahbarangnya=0;
        for (int i=0;i<cart.size();i++)
        {
            jumlahbarangnya+=cart.get(i).getJumlah();
        }
        return  jumlahbarangnya;
    }


}
