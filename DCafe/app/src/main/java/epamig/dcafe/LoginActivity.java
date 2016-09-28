package epamig.dcafe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContextWrapper;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.HttpGet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import epamig.dcafe.model.Usuario;
import epamig.dcafe.sistema.Aplicacao;

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private AlertDialog alerta;
    Usuario SuperUsuario = new Usuario();

    public String IP = new Aplicacao().getIP();

    private String url = IP + "usuarios.php?email=";


    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.txtEmail);

        mPasswordView = (EditText) findViewById(R.id.txtSenha);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.btLogar);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mCadastrar = (Button) findViewById(R.id.btCadastrar);
        mCadastrar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, CadastroActivity.class);
                startActivity(intent);

            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute(url + email);

        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    public class UserLoginTask extends AsyncTask<String, Void, Usuario> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Usuario doInBackground(String... params) {
            return RetornaUsuario(params[0]);
        }

        @Override
        protected void onPostExecute(final Usuario result) {
            mAuthTask = null;
            showProgress(false);

            super.onPostExecute(result);

            SuperUsuario = result;

            if (SuperUsuario.getSenhaUsuario() == null || SuperUsuario.getSenhaUsuario().isEmpty() || SuperUsuario.getSenhaUsuario().equals("")) {

                mEmailView.setError("Email não cadastrado");
                mEmailView.requestFocus();

            } else {
                String senhaMd5 = md5(mPassword);
                if (SuperUsuario.getSenhaUsuario().equals(senhaMd5)) {
                    SalvarLogin(SuperUsuario);
                    finish();
                    Intent intent = new Intent();

              /*  if(BancoExiste(LoginActivity.this, getString(R.string.nomeBanco)) == true){
                    intent.setClass(LoginActivity.this, Principal.class);
                }else {
                */
                    intent.setClass(LoginActivity.this, SincronizacaoActivity.class);
                    //}
                    startActivity(intent);

                } else {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }
            }
        }


        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public String EncondigURl(String url) {
        String texto = url.trim().replace("@", "%40");
        return texto;

    }

    public Usuario RetornaUsuario(String url) {

        String urlEncondig = EncondigURl(url);
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(urlEncondig);
        try {
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                String json = getStringFromInputStream(instream);
                instream.close();
                Usuario usuario = getUsuario(json);
                return usuario;
            }
        } catch (Exception e) {
            Log.e("Erro", "Falha ao acessar Web service", e);
        }
        return null;
    }

    //Converte objeto InputStream para String
    private String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }

    //Retorna um usuario com as informações retornadas do JSON
    private Usuario getUsuario(String jsonString) {
        Usuario usuarioObjeto = new Usuario();
        try {
            JSONObject objJson = new JSONObject(jsonString);
            JSONArray UsuariosJson = objJson.getJSONArray("usuarios");

            JSONObject usuario = new JSONObject(UsuariosJson.getString(0));

            usuarioObjeto.setSucesso(usuario.getInt("successo"));
            usuarioObjeto.setIdUsuario(usuario.getInt("idUsuario"));
            usuarioObjeto.setNomeUsuario(usuario.getString("nomeUsuario"));
            usuarioObjeto.setEmailUsuario(usuario.getString("emailUsuario"));
            usuarioObjeto.setProfissaoUsuario(usuario.getString("profissaoUsuario"));
            usuarioObjeto.setSenhaUsuario(usuario.getString("senhaUsuario"));
        } catch (JSONException e) {
            Log.i("ERRO", e.toString());
        }
        return usuarioObjeto;
    }

    public static String md5(String input) {

        String md5 = null;

        if (null == input) return null;

        try {

            //Create MessageDigest object for MD5
            MessageDigest digest = MessageDigest.getInstance("MD5");

            //Update input string in message digest
            digest.update(input.getBytes(), 0, input.length());

            //Converts message digest value in base 16 (hex)
            md5 = new BigInteger(1, digest.digest()).toString(16);

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        }
        return md5;
    }

    //-----------------------------Salvando dados no SharedPreferences----------------------------//
    public void SalvarLogin(Usuario usuario) {

        SharedPreferences settings = getSharedPreferences(getString(R.string.preferences), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("idUsuario", usuario.getIdUsuario());
        editor.putString("nomeUsuario", usuario.getNomeUsuario());
        editor.putString("emailUsuario", usuario.getEmailUsuario());
        editor.putString("profissaoUsuario", usuario.getProfissaoUsuario());
        editor.putString("senhaUsuario", usuario.getSenhaUsuario());

        editor.commit();
    }

    public static boolean BancoExiste(ContextWrapper context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    private void loginFalhou() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Usuário não cadastrado");
        builder.setMessage("Cadastre-se primeiro");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        alerta = builder.create();
        alerta.show();
    }
}


