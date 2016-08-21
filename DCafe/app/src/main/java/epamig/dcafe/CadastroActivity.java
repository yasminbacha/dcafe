package epamig.dcafe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;
import epamig.dcafe.model.Usuario;
import epamig.dcafe.sistema.Aplicacao;


public class CadastroActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private UserLoginTask mAuthTask = null;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    private EditText edtNome;
    private EditText edtEmail;
    private EditText edtOutraAtividade;
    private EditText edtSenha;


    private String atividadeProfissional;
    private RadioGroup rgAtividade;
    private View mProgressView;
    private View mLoginFormView;

    public String IP = new Aplicacao().getIP();

    private String url = IP + "inserirusuario.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        //--------Criar seta para voltar---------------------------------------------------------//
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtNome = (EditText) findViewById(R.id.edtNomeCadastro);
        edtEmail = (EditText) findViewById(R.id.edtEmailCadastro);
        edtOutraAtividade = (EditText) findViewById(R.id.edtOutraCadastro);
        edtSenha = (EditText) findViewById(R.id.edtSenhaCadastro);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        rgAtividade = (RadioGroup) findViewById(R.id.rgProfissao);
        Button mEmailSignInButton = (Button) findViewById(R.id.btCadastro);
        atividadeProfissional = getString(R.string.atividade_extensionista);

        //--------Deixar Campo invisivel----------------------------------------------------------//
        edtOutraAtividade.setVisibility(View.GONE);

        rgAtividade.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = rgAtividade.findViewById(checkedId);
                int index = rgAtividade.indexOfChild(radioButton);

                switch (index) {
                    case 0:
                        edtOutraAtividade.setVisibility(View.GONE);
                        atividadeProfissional = getString(R.string.atividade_extensionista);
                        break;
                    case 1:
                        edtOutraAtividade.setVisibility(View.GONE);
                        atividadeProfissional = getString(R.string.atividade_epamig);
                        break;
                    case 2:
                        edtOutraAtividade.setVisibility(View.GONE);
                        atividadeProfissional = getString(R.string.atividade_geosolos);
                        break;
                    case 3:
                        edtOutraAtividade.setVisibility(View.GONE);
                        atividadeProfissional = getString(R.string.atividade_cafeicultor);
                        break;
                    case 4:
                        edtOutraAtividade.setVisibility(View.GONE);
                        atividadeProfissional = getString(R.string.atividade_cooperativa);
                        break;
                    case 5:
                        edtOutraAtividade.setVisibility(View.VISIBLE);
                        edtOutraAtividade.requestFocus();
                        atividadeProfissional = getString(R.string.atividade_outra);
                        break;
                }
            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    //----------------------Criar botão voltar na toolbar-----------------------------------------//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    //--------------------------Função assincrona para cadastro ----------------------------------//
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        edtNome.setError(null);
        edtEmail.setError(null);
        edtOutraAtividade.setError(null);
        edtSenha.setError(null);

        // Store values at the time of the login attempt.
        String nome = edtNome.getText().toString();
        String email = edtEmail.getText().toString();
        String outraAtividade = edtOutraAtividade.getText().toString();
        String senha = edtSenha.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //Cheque EdtNome é vazio
        if (TextUtils.isEmpty(nome)) {
            edtNome.setError(getString(R.string.error_field_required));
            focusView = edtNome;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError(getString(R.string.error_field_required));
            focusView = edtEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            edtEmail.setError(getString(R.string.error_invalid_email));
            focusView = edtEmail;
            cancel = true;
        }

        // Checando uma senha válida
        if (TextUtils.isEmpty(senha)) {
            edtSenha.setError(getString(R.string.error_field_required));
            focusView = edtSenha;
            cancel = true;
        } else if (!isPasswordValid(senha)) {
            edtSenha.setError(getString(R.string.error_invalid_password));
            focusView = edtSenha;
            cancel = true;
        }


        if (atividadeProfissional.equals(getString(R.string.atividade_outra)) == true) {

            if (TextUtils.isEmpty(outraAtividade)) {
                edtOutraAtividade.setError(getString(R.string.error_field_required));
                focusView = edtOutraAtividade;
                cancel = true;
            } else {
                atividadeProfissional = outraAtividade;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(nome, email, senha, atividadeProfissional);
            mAuthTask.execute((Void) null);
        }
    }


    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }


    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mNome;
        private final String mEmail;
        private final String mSenha;
        private final String mAtividadeProfissional;
        private Usuario mUsuario;

        UserLoginTask(String nome, String email, String senha, String atividadeProfissional) {
            mNome = nome;
            mEmail = email;
            mSenha = md5(senha);
            mAtividadeProfissional = atividadeProfissional;
            mUsuario = new Usuario();
            mUsuario.setNomeUsuario(mNome);
            mUsuario.setNomeUsuario(mEmail);
            mUsuario.setNomeUsuario(mAtividadeProfissional);
            mUsuario.setNomeUsuario(mSenha);

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);

            ArrayList<NameValuePair> valores = new ArrayList<NameValuePair>();
            valores.add(new BasicNameValuePair("nomeUsuario", mNome));
            valores.add(new BasicNameValuePair("emailUsuario", mEmail));
            valores.add(new BasicNameValuePair("profissaoUsuario", mAtividadeProfissional));
            valores.add(new BasicNameValuePair("senhaUsuario", mSenha));
            try {
                post.setEntity(new UrlEncodedFormEntity(valores));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                HttpResponse resposta = client.execute(post);
                String json = EntityUtils.toString(resposta.getEntity());
                try {
                    JSONObject objJson = new JSONObject(json);
                    JSONArray UsuariosJson = objJson.getJSONArray("usuarios");
                    JSONObject usuario = new JSONObject(UsuariosJson.getString(0));
                    mUsuario.setIdUsuario(usuario.getInt("idUsuario"));
                    salvarLogin(mUsuario);
                } catch (JSONException e) {
                    Log.i("ERRO", e.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();

            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
                Intent intent = new Intent();
                intent.setClass(CadastroActivity.this, SincronizacaoActivity.class);
                startActivity(intent);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
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
    public void salvarLogin(Usuario usuario) {

        SharedPreferences settings = getSharedPreferences(getString(R.string.preferences), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("idUsuario", usuario.getIdUsuario());
        editor.putString("nomeUsuario", usuario.getNomeUsuario());
        editor.putString("emailUsuario", usuario.getEmailUsuario());
        editor.putString("profissaoUsuario", usuario.getProfissaoUsuario());
        editor.putString("senhaUsuario", usuario.getSenhaUsuario());

        editor.commit();
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
                new ArrayAdapter<>(CadastroActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


}

