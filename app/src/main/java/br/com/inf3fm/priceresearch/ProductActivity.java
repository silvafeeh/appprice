package br.com.inf3fm.priceresearch;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProductActivity extends AppCompatActivity {

    public static final String TAG = "Product Activity";

    public String TEXT_SEARCH = "caracteres da busca";

    // vamos ADAPTAR os dados da tabela do banco de dados
    // para visualiar no layout TELA
    public ProductAdapter mProductAdapter;

    TextView mTextViewFullName, mTextViewTotalValue;

    FloatingActionButton mFabAdd;

    SharedPreferences mSharedPreferences;  // será usado para saber se logado ou não

    // funcionalidade para gerenciar o resultado do EDIT ou ADD
    // que foi feito pelo usuario na tela ADD_EDIT_PRODUCT
    // *** AVISO *** é a funcionalide chave desta classe
    ActivityResultLauncher<Intent> mActivityResultLauncher =
    registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                        if( result.getResultCode() == RESULT_OK && result.getData() != null ){

                            Bundle mBundleResponse = result.getData().getExtras();  //ex.  EXTRA_ID EXTRA_NAME

                            int vId = mBundleResponse.getInt("EXTRA_ID" , -1);

                            String mName = mBundleResponse.getString("EXTRA_NAME");

                            float vPrice = mBundleResponse.getFloat("EXTRA_PRICE" , 0.0f);

                            float vPerception = mBundleResponse.getFloat("EXTRA_PERCEPTION" ,  0.0f);

                            int vCycle = mBundleResponse.getInt("EXTRA_CYCLE" , 0);

                            int vFrequency = mBundleResponse.getInt("EXTRA_FREQUENCY" , 0);

                            String mMode = mBundleResponse.getString("EXTRA_MODE");

                            String mMessage = "Estou fazendo Insert ou Update (resumindo CRUD)";

                            if( mMode.equals("EDIT")  ) {
                                Product mProduct = new Product(vId , mName , vPrice , vPerception , 1 , 0 , vFrequency , vCycle , 0 );
                                int vResult = ProductDao.updateProduct( mProduct , getApplicationContext());
                                if(vResult <= 0){
                                    mMessage = "Um erro foi encontrado ao atualizar o produto";
                                } else {
                                    mMessage = "Produto atualizado com sucesso";
                                    setupRecyclerViewAdapter();

                                }

                            } else {

                                Product mProduct = new Product(mName , vPrice , vPerception , 1 , 0 , vFrequency , vCycle , 0 );
                                int vResult = ProductDao.insertProduct( mProduct , getApplicationContext());
                                if(vResult <= 0){
                                    mMessage = "Um erro foi encontrado ao inserir o produto";
                                } else {
                                    mMessage = "Produto inserido com sucesso";
                                    setupRecyclerViewAdapter();

                                }


                            }
                            mTextViewTotalValue.setText("R$ 0,00");
                            Toast.makeText(ProductActivity.this , mMessage , Toast.LENGTH_SHORT).show();



                        } else if( result.getResultCode() == RESULT_CANCELED) {
                            Toast.makeText(ProductActivity.this , "Cancelado pelo usuário" , Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProductActivity.this , "Erro inesperado" , Toast.LENGTH_SHORT ).show();

                        }
                }
            });



    // codigo para escutar o clique de um item na listagem
    public View.OnClickListener mOnItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder mViewHolder = (RecyclerView.ViewHolder) v.getTag();

            int vPosition = mViewHolder.getAdapterPosition();

            Intent mIntent = new Intent(getApplicationContext() , ProductAddEditActivity.class);

            mIntent.putExtra("EXTRA_ID"  , mProductAdapter.getProductAt(vPosition).getId()  );
            mIntent.putExtra("EXTRA_NAME"  , mProductAdapter.getProductAt(vPosition).getName()  );
            mIntent.putExtra("EXTRA_PRICE"  , mProductAdapter.getProductAt(vPosition).getPrice()  );
            mIntent.putExtra("EXTRA_PERCEPTION"  , mProductAdapter.getProductAt(vPosition).getRating()  );
            mIntent.putExtra("EXTRA_CYCLE"  , mProductAdapter.getProductAt(vPosition).getAmountConsumption()  );
            mIntent.putExtra("EXTRA_FREQUENCY"  , mProductAdapter.getProductAt(vPosition).getConsumptionCycle()  );

            mActivityResultLauncher.launch(mIntent);


        }
    };



    private void setupRecyclerViewAdapter() {
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView_products);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setHasFixedSize(true);

        mProductAdapter = new ProductAdapter(getApplicationContext() , ProductDao.listAllProducts(getApplicationContext()) , mTextViewTotalValue );

        mRecyclerView.setAdapter(mProductAdapter);

        mProductAdapter.setProductList(ProductDao.listAllProducts(getApplicationContext()));

        mProductAdapter.setOnClickListener(mOnItemClickListener);



    }


    // codigo de verificao se usuario já logou alguma vez na vida
    private void verifyNotLogged(){
        if( mSharedPreferences.getString("logged" , "false").equals("false") ) {
            showLogin();
        }  else {
            mTextViewFullName.setText( mSharedPreferences.getString("fullName", "") );
            setupRecyclerViewAdapter();
        }


    }

    private void showLogin() {
        Intent mIntent = new Intent(getApplicationContext() , LoginActivity.class);
        startActivity(mIntent);
        finish();

    }


    // codigo para o botão ou item de menu para fazer logoff
    private void exitAppLogoff(){
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString("logged"   , "");
        mEditor.putString("email"    , "");
        mEditor.putString("fullName" , "");
        mEditor.apply();
        showLogin();

    }

    public class ClickMyButtonItemMenuLogoff implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            exitAppLogoff();
        }
    }

    private void showAddEditProduct(){
        Intent mIntent = new Intent(ProductActivity.this ,  ProductAddEditActivity.class);
        mActivityResultLauncher.launch(mIntent);
    }

    public class ClickMyButtonItemMenuAddEdit implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            showAddEditProduct();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.main_menu , menu);

        SearchManager mSearchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView mSearchView = (SearchView) menu.findItem(R.id.action_main_menu_search).getActionView();
        // aviso - aqui é comum erro  - lembra de usar SearchView androidx

        mSearchView.setSearchableInfo(mSearchManager.getSearchableInfo(getComponentName()));

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                TEXT_SEARCH = query;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mProductAdapter.getFilter().filter(newText);

                return false;
            }
        });



        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_add:
                showAddEditProduct();
                return true;
            case R.id.action_display_all_completed:
                //showAllProducts();
                return true;
            case R.id.action_display_all_deleted:
                //showAllDeletedProducts();
                return true;
            case R.id.action_delete_all:
                //executeDeleteAllProducts();
                ProductDao.deleteAllProducts(getApplicationContext());

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }


    private void saveProduct(){
        Intent mIntent = new Intent(ProductActivity.this , ProductAddEditActivity.class);
        mActivityResultLauncher.launch(mIntent);


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_products);

        mTextViewFullName = findViewById(R.id.textView_fullName);
        Intent mIntent = getIntent();
        if(mIntent.hasExtra("EXTRA_FULL_NAME")){
            mTextViewFullName.setText(mIntent.getStringExtra("EXTRA_FULL_NAME"));
        }

        mFabAdd = findViewById(R.id.fab_add_product);
        mFabAdd.setOnClickListener(new ClickMyButtonItemMenuAddEdit());

        mTextViewTotalValue = findViewById(R.id.textView_total_value);

        mSharedPreferences = getSharedPreferences("MyAppName" , MODE_PRIVATE);

        verifyNotLogged();

    }
}
















