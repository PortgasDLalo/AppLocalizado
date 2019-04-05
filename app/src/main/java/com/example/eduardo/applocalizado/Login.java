package com.example.eduardo.applocalizado;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Login extends AppCompatActivity {

    private EditText usuario1, password;
    private Button login, registrar;
    private AsyncHttpClient cliente;
    private DAOHelper d;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        d=new DAOHelper(this);
        usuario1=(EditText)findViewById(R.id.usuario);
        password=(EditText)findViewById(R.id.password);
        login=(Button)findViewById(R.id.login);
        cliente = new AsyncHttpClient();

        //list=(ListView) findViewById(R.id.lista1);

        botonLogin();
        if (permisos()) {
            //enviar();
        } else {
            Toast.makeText(this, "Habilite los permisos", Toast.LENGTH_LONG).show();

        }
        //listarDatos();

    }

    public void listarDatos()
    {
        final ArrayList<usuario> lista = d.obtenerProducto();
        if (!lista.isEmpty())
        {
            list.setVisibility(View.VISIBLE);
            ArrayAdapter<usuario> adapter = new ArrayAdapter<usuario>(Login.this, android.R.layout.simple_list_item_1, lista);
            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    usuario p = lista.get(position);
                    /*id1 = p.getId();
                    producto.setText(p.getProducto());
                    precio.setText(String.valueOf(p.getPrecio()));
                    cantidad.setText(""+p.getCantidad());
                    nota.setText(p.getNota());
                    agregar.setEnabled(false);
                    modificar.setEnabled(true);
                    eliminar.setEnabled(true);*/
                }
            });
        }else
        {
            list.setVisibility(View.INVISIBLE);
        }
    }
    private void botonLogin()
    {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setEnabled(false);
                if (usuario1.getText().toString().isEmpty() || password.getText().toString().isEmpty())
                {
                    Toast.makeText(Login.this, "Hay Campos Vacios", Toast.LENGTH_SHORT).show();
                    login.setEnabled(true);
                }else
                {
                    final String usu = usuario1.getText().toString().replace(" ","%20");
                    final String pas = password.getText().toString().replace(" ","%20");
                    String url = "https://localizadorazl1.000webhostapp.com/entrarLocalizado.php?usuarioloc="+usu+"&contraseñaloc="+pas;
                    cliente.post(url, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode==200)
                            {
                                String respuesta = new String(responseBody);
                                if (respuesta.equalsIgnoreCase("null"))
                                {
                                    Toast.makeText(Login.this, "Error de Usuario y/o Contraseña", Toast.LENGTH_SHORT).show();
                                    usuario1.setText("");
                                    password.setText("");
                                    login.setEnabled(true);
                                }
                                else
                                {
                                    try {
                                        SQLiteDatabase db = d.getWritableDatabase();
                                        usuario u = new usuario();
                                        int id = 1;
                                        if (db!=null)
                                        {
                                            Cursor c = db.rawQuery(" SELECT usuario1,contraseña1 FROM tablaUsuario WHERE id=1",null);
                                            if (c != null) {
                                                c.moveToFirst();
                                                do {
                                                    //Asignamos el valor en nuestras variables para usarlos en lo que necesitemos
                                                    /*String user = c.getString(c.getColumnIndex("usuario1"));
                                                    String contra = c.getString(c.getColumnIndex("contraseña1"));
                                                    Toast.makeText(Login.this, ""+user+"   "+contra, Toast.LENGTH_SHORT).show();*/
                                                    u.setId(id);
                                                    u.setUsuario(usuario1.getText().toString());
                                                    u.setContraseña(password.getText().toString());
                                                    int res1 = d.modificarUsuario(u);
                                                    if (res1==1)
                                                    {
                                                        Toast.makeText(Login.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                                                        //listarDatos();

                                                    }
                                                } while (c.moveToNext());
                                            }else
                                            {
                                                u.setId(id);
                                                u.setUsuario(usuario1.getText().toString());
                                                u.setContraseña(password.getText().toString());
                                                long res = d.agregarid(u);
                                                if (res!=-1)
                                                {
                                                    Toast.makeText(Login.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                                                    //listarDatos();
                                                }
                                            }

                                            //Cerramos el cursor y la conexion con la base de datos
                                            c.close();
                                            db.close();
                                        }

                                        JSONObject jsonObject = new JSONObject(respuesta);
                                        /*Usuario u = new Usuario();
                                        u.setId(jsonObject.getInt("id"));
                                        u.setUsuario(jsonObject.getString("usuario"));
                                        u.setContraseña(jsonObject.getString("contraseña"));
                                        u.setNombre(jsonObject.getString("nombre"));
                                        u.setDireccion(jsonObject.getString("direccion"));
                                        u.setCiudad(jsonObject.getString("ciudad"));
                                        u.setEstado(jsonObject.getString("estado"));
                                        u.setCorreo(jsonObject.getString("correo"));
                                        u.setTelefono(jsonObject.getString("telefono"));*/
                                        Intent intent = new Intent(Login.this, MyService.class);
                                        ////startService(new Intent(Login.this, MyService.class));
                                        //intent.putExtra("usuario", usuario1.getText().toString());
                                        //intent.putExtra("contraseña", password.getText().toString());
                                        Login.this.startService(intent);
                                        login.setEnabled(false);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            login.setEnabled(true);
                        }
                    });
                }
            }
        });
    }


    private boolean permisos() {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        if ((shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))) {
            cargarDialogo();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
        return false;
    }


    private void cargarDialogo() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle("Permisos desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la aplicación");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                }
            }
        });
        dialogo.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //enviar();
            } else {
                solicitarPermisosManual();
            }

        }
    }

    private void solicitarPermisosManual() {
        final CharSequence[] opciones = {"si", "no"};
        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(this);
        alertOpciones.setTitle("Seleccione una opción");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } else {
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }
}
