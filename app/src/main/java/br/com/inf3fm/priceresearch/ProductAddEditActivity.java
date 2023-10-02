package br.com.inf3fm.priceresearch;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class ProductAddEditActivity extends AppCompatActivity {

    public static final String TAG = "Tela para Add Edit Product";

    //objeto para definir o tipo de uso  Add   ou   Edit
    String mMode;  //"ADD"   "EDIT"
    TextView mTextViewCancel;
    Button mButtonSave;
    EditText mEditTextName, mEditTextPrice;
    RatingBar mRatingBarPrice;
    Slider mSliderCycle;
    ChipGroup mChipGroup;
    Chip mChip0, mChip1, mChip2, mChip3, mChip4;
    // como vou controlar dados em memoria - criar variaveis
    int vSliderValue, vChipValue;
    String mMessage;

    //acao quando clicou em CANCEL
    private void performCancel(){
        Intent mIntent = new Intent();
        setResult(RESULT_CANCELED , mIntent);
        finish();
    }

    public class ClickMyCancel implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            performCancel();
        }
    }


    //regra para verificar a entrada de dados
    private boolean isInputValid(){
        if(TextUtils.isEmpty(mEditTextName.getText())  ||  TextUtils.isEmpty(mEditTextPrice.getText())  || vChipValue == 0  ){
            return true;
        } else {
            return false;
        }

    }


    private void saveProduct() {
        if(isInputValid()) {
            mMessage = "Informe Nome ou Preço ou Frequencia";
            Snackbar.make( findViewById(R.id.layout_add_edit_product) , mMessage , Snackbar.LENGTH_SHORT).show();
            return;
        }
        Intent mIntentResponse = new Intent();
        mMode = "ADD"; //ao salvar farei um INSERT
        int vId = getIntent().getIntExtra("EXTRA_ID" , -1 );
        if( vId != -1 ){
            mMode = "EDIT";  //UPDATE
            mIntentResponse.putExtra("EXTRA_ID"  , vId);
        }
        String mName = mEditTextName.getText().toString();
        mIntentResponse.putExtra("EXTRA_NAME"   , mName);

        float vPrice = Float.valueOf(mEditTextPrice.getText().toString());
        mIntentResponse.putExtra("EXTRA_PRICE" , vPrice);

        float vPerception = Float.valueOf(mRatingBarPrice.getRating());
        mIntentResponse.putExtra("EXTRA_PERCEPTION" , vPerception);

       mIntentResponse.putExtra("EXTRA_CYCLE" , vSliderValue);

       mIntentResponse.putExtra("EXTRA_FREQUENCY" , vChipValue);

       mIntentResponse.putExtra("EXTRA_MODE" ,  mMode);

       setResult(RESULT_OK , mIntentResponse);
       finish();

    }

    public class ClickMyButtonSave implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            saveProduct();
        }
    }


    public class SliderMySlide implements Slider.OnChangeListener{
        @Override
        public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
             vSliderValue = (int) value;
             mChip0.setText( vSliderValue + "X");
        }
    }

    public class ChipGroupSelectionChip implements ChipGroup.OnCheckedStateChangeListener{
        @Override
        public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
             switch (group.getCheckedChipId()){
                 case R.id.chip_consumption_cycle_1: vChipValue=1 ; break;
                 case R.id.chip_consumption_cycle_2: vChipValue=2 ; break;
                 case R.id.chip_consumption_cycle_3: vChipValue=3 ; break;
                 case R.id.chip_consumption_cycle_4: vChipValue=4 ; break;
                 default: vChipValue=0;
             }
        }
    }


    private void setChipNumber(int vChipValue){
        switch (vChipValue){
            case 1: mChip1.setChecked(true); break;
            case 2: mChip2.setChecked(true); break;
            case 3: mChip3.setChecked(true); break;
            case 4: mChip4.setChecked(true); break;
        }

    }


    // Precisa formatar o price


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        mTextViewCancel = findViewById(R.id.textView_cancel_product_add_edit);
        // fazer o escutador
        // 07-08 2 aulas
        mTextViewCancel.setOnClickListener(new ClickMyCancel());

        mButtonSave = findViewById(R.id.button_save_product);
        mButtonSave.setOnClickListener(new ClickMyButtonSave());

        mEditTextName = findViewById(R.id.editText_product_name);

        mEditTextPrice = findViewById(R.id.editText_product_price);

        mRatingBarPrice = findViewById(R.id.rating_product_price_perception);

        mSliderCycle = findViewById(R.id.slider_consumption_cycle);
        mSliderCycle.addOnChangeListener(new SliderMySlide());

        mChipGroup = findViewById(R.id.chip_group_option);
        mChipGroup.setOnCheckedStateChangeListener(new ChipGroupSelectionChip());

        mChip0 = findViewById(R.id.chip_consumption_cycle_0);
        mChip1 = findViewById(R.id.chip_consumption_cycle_1);
        mChip2 = findViewById(R.id.chip_consumption_cycle_2);
        mChip3 = findViewById(R.id.chip_consumption_cycle_3);
        mChip4 = findViewById(R.id.chip_consumption_cycle_4);

        Intent mIntentShow = getIntent();
        // obtendo a intenção/ação com a tela

        // testar  o tipo de ação  EDIT ou ADD
        // EDIT indica a edição de algo que existe
        // ADD  indica a digitação de algo novo

        if( mIntentShow.hasExtra("EXTRA_ID") ){
            setTitle("Edit Product");
            mEditTextName.setText(mIntentShow.getStringExtra("EXTRA_NAME"));

            double value = mIntentShow.getDoubleExtra("EXTRA_PRICE" , 0);
            // isso é errado - mEditTextPrice.setText("R$" + value);
            String mStringPrice = String.format(java.util.Locale.ROOT , "%.2f" , value);
            mEditTextPrice.setText(mStringPrice);

            // aviso - é necessário criar um método para limitar a
            // digitação de valores após o separador de
            // casas decimais

            //https://pt.stackoverflow.com/questions/279149/tratamento-casas-decimais-edittext

            mRatingBarPrice.setRating(mIntentShow.getFloatExtra("EXTRA_PERCEPTION"  , 0.0f) );

            //mSliderCycle.setValue(mIntentShow.getFloatExtra("EXTRA_CYCLE" , 1));
            vSliderValue = mIntentShow.getIntExtra("EXTRA_CYCLE" , 1);
            mSliderCycle.setValue(vSliderValue);

            vChipValue = mIntentShow.getIntExtra("EXTRA_FREQUENCY" , 0);
            setChipNumber(vChipValue);

        } else {
            setTitle("New Product");
            vSliderValue = 1;
            vChipValue = 0;

        }

    }

    // código para usar o menu (...)
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.save_menu , menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_menu_save:
                saveProduct();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

}
