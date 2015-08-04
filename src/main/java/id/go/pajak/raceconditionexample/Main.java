package id.go.pajak.raceconditionexample;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;
import java.util.Stack;
import oracle.jdbc.OracleDriver;

/**
 * 
 *  untuk simulasi race condition dalam pengambilan nomer SKT menggunakan package oracle
 * 
 */
public class Main {

    /**
     *  query untuk call oracle package
     */
    private static String S = "CALL PKG_SEQ.GET_NO_DOK(? , ? , ?)";

    private static Connection getConnection() throws Throwable {
        Class.forName(OracleDriver.class.getName());
        return DriverManager.getConnection("jdbc:oracle:thin:piloting/password@//localhost:1521/XE");
    }

    /**
     * inner class untuk thread
     */
    private static class ThreadNoDoc extends Thread {

        private final String type;
        private final String kdKpp;
        private final Stack<String> stack;

        public ThreadNoDoc(String name, String type, String kdKpp, Stack stack) {
            super(name);
            this.type = type;
            this.kdKpp = kdKpp;
            this.stack = stack;
        }

        @Override
        public void run() {
            Connection con = null;
            CallableStatement callable = null;
            try {
                con = getConnection();
                while (true) {
                    long tm = System.currentTimeMillis();
                    callable = con.prepareCall(S);
                    callable.setMaxRows(1);
                    callable.setFetchSize(1);
                    callable.setString(1, type);
                    callable.setString(2, kdKpp);
                    callable.registerOutParameter(3, Types.VARCHAR);
                    callable.executeUpdate();
                    String trxNo = callable.getString(3);
                    tm = System.currentTimeMillis() - tm;
                    if (stack.contains(trxNo)) {
                        System.out.println(getName()+ " query nomer surat " + trxNo + " for "+ tm + " ms " + " -- > Duplicate");
                    } else {
                        System.out.println(getName()+ " query nomer surat " + trxNo + " for "+ tm + " ms ");
                        stack.add(trxNo);
                    }
                    try {
                        callable.close();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    Thread.sleep(1000);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                try {
                    con.close();
                } catch (Throwable t) {
                    t.printStackTrace();
                }

                try {
                    callable.close();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Throwable {
        Stack<String> stack = new Stack<>();
        ThreadNoDoc t1 = new ThreadNoDoc("Thread1", "RG02", "511", stack);
        ThreadNoDoc t2 = new ThreadNoDoc("Thread2", "RG02", "511", stack);

        t1.start();
        /*
         * ubah value dibawah ini untuk simulasi selisih waktu antar query
         */
        Thread.sleep(200);
        t2.start();
    }
}
