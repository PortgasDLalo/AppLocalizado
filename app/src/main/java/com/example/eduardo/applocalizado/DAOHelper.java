package com.example.eduardo.applocalizado;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DAOHelper extends SQLiteOpenHelper {
    private static String DB = "Datos";
    private static String tabla = "tablaUsuario";

    public DAOHelper(Context context)
    {
        super(context, DB, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String crearTabla = "CREATE TABLE "+tabla+"(id INTERGER PRIMARY KEY, usuario1 VARCHAR, contraseña1 VARCHAR)";
        db.execSQL(crearTabla);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long agregarid(usuario u)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", u.getId());
        cv.put("usuario1", u.getUsuario());
        cv.put("contraseña1", u.getContraseña());
        long res = db.insert(tabla, null, cv);
        db.close();
        return res;
    }

    public int modificarUsuario(usuario us)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("usuario1", us.getUsuario());
        cv.put("contraseña1", us.getContraseña());
        int res = db.update(tabla, cv, "id=?", new String[]{Integer.toString(us.getId())});
        db.close();
        return res;
    }

    public void seleccionar()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //String[] args = new String[]{"1"};
        String[] campos = new String[]{"usuario", "contraseña"};
        //Cursor dato = db.query(tabla,campos, "id=?", args, null, null, null);
        Cursor dato=  db.rawQuery(" SELECT usuario1,contraseña1 FROM tablaUsuario WHERE id=1",null);
        dato.moveToFirst();
        usuario us1 = new usuario();
        us1.setUsuario(dato.getString(0));
        us1.setContraseña(dato.getString(1));
        dato.close();
        //return dato;
    }

    public ArrayList<usuario> obtenerProducto()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor datos = db.rawQuery("SELECT * FROM "+tabla+" ORDER BY id ASC", null);
        ArrayList<usuario> lista = new ArrayList<usuario>();
        while (datos.moveToNext())
        {
            usuario p = new usuario();
            p.setId(datos.getInt(0));
            p.setUsuario(datos.getString(1));
            p.setContraseña(datos.getString(2));
            lista.add(p);
        }
        db.close();
        return lista;
    }
}
