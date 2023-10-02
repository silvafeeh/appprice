package br.com.inf3fm.priceresearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements Filterable {

    //marcacao  psfs
    public static final String TAG = "Product Adapter";

    // UM AVISO - essa classe é a campea de erros (por erro na digitacao)

    //objetos para controlar o click e long click
    private View.OnClickListener mOnClickListener; // objeto para controlar/escutar/ouvir o click
    private View.OnLongClickListener mOnLongClickListener; // objeto para ouvir/escutar/controlar o clique longo

    //objeto para controlar o contexto do uso do app
    private Context mContext;

    // objeto para a lista de : produtos
    private List<Product> mProductList;

    // objeto para a lista de produtos em MEMÓRIA - LISTA COMPLETA para o cancelamento do FILTRO
    private List<Product> mProductListFull;

    private TextView mTextViewTotalPrice; // objeto que vai guardar o total da compra

    // Criar o construtor da classe - para em outra classe utilizar   =new


    public ProductAdapter(Context context, List<Product> productList, TextView textViewTotalPrice) {
        mContext = context;
        mProductList = productList;
        mTextViewTotalPrice = textViewTotalPrice;
    }

    // a funcionalidade/metodo a seguir NÃO É RECOMENDADO FAZER AQUI
    // isso devido uma tecnica de programacao chamada CLEAN CODE
    // essa funcionalide é uma regra de negocio (Business Logic)
    // 5 estrelas preco verde , 4 verde , 3 verde , 2 vermelho , 1 vermelho , 0 vermelho
    public String setPriceColor(double vRating){
        if(vRating < 3){
            return "#BF0404"; //vermelho
        } else {
            return "#00FF00"; // verde
        }

    }








    @Override
    public Filter getFilter() {
        return applyProductFilter;
    }

    @NonNull
    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        View mItemView = mLayoutInflater.inflate(R.layout.card_item_product_list , parent , false);
        

        return new ProductViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int position) {
        // vinculo (ligacao) dos objetos java com os elementos da tela do CARDVIEW (item)
        // preciso idenficar qual é o item da lista
        // criar um objeto para associar o item da lista
        Product mProductCurrent = mProductList.get(position);
        // ligar = holder os objetos Java com os elementos da tela (Layout)
        //holder.mImageViewProduct.setImageBitmap("pig.jpg");  //  pesquise sobre Glide
        holder.mTextViewProductName.setText(mProductCurrent.getName());
        holder.mTextViewProductPrice.setText( "" + mProductCurrent.getPrice()  );
        holder.mRatingBarProduct.setRating(mProductCurrent.getRating());


    }

    @Override
    public int getItemCount() {
        if( mProductList != null ) {
            return mProductList.size();
        } else {

            return 0;
        }
    }

    // funcionalidade / ação para notificar a mudança da lista quando faço pesquisa
    // aqui vou definir uma lista completa com todos os itens da lista
    // é uma cópia em memória da lista - vantagem para diminuir acesso ao Banco de dados
    public void setProductList( List<Product> mProducts ) {
        mProductList = mProducts;
        mProductListFull = new ArrayList<>(mProducts); //esse objeto em memória
        notifyDataSetChanged();
    }


    // funcionalidade / ação para identificar um produto na lista  - pegar a posição na lista
    public Product getProductAt(int vPosition){
        return  mProductList.get(vPosition);
    }

    // acao para definir o escutador/ouvinte do clique no item da lista
    public void setOnClickListener(View.OnClickListener mItemClickListener) {
        mOnClickListener = mItemClickListener;
    }

    // acao para definir o escutador/ouvinte do clique LONGO (pressionamento) no item da lista
    public void setOnLongClickListener(View.OnLongClickListener mItemLongClickListener){
        mOnLongClickListener = mItemLongClickListener;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        // aqui irao os objetos que representam os elementos do CARDVIEW
        private final ImageView mImageViewProduct;
        private final TextView mTextViewProductName;
        private final TextView mTextViewProductPrice;
        private final RatingBar mRatingBarProduct;
        private final Button mButtonProductAdd;
        private final Button mButtonProductQuantity;
        private final Button mButtonProductRemove;
        // variaveis para controlar o total da compra e a quantidade de cada item da lista
        double vTotalPrice = 0.0;
        int vQuantity = 0;

        // funcionalidade / ação / metodo - soma total da compra
        // Deverá ser alterado o local desse codigo (Regra de Negocio)
        // regra : mostrar a soma dos itens que serao comprados
        private void showTotalPrice(){
            vTotalPrice = 0.0;
            for( int i=0 ; i <= mProductList.size() -1 ; i++ ){
                vTotalPrice = vTotalPrice + mProductList.get(i).getPrice() * mProductList.get(i).getUnit();
            }
            NumberFormat mNumberFormat = NumberFormat.getCurrencyInstance(new Locale("pt" , "BR"));
            String mStringValue = mNumberFormat.format(vTotalPrice);
            mTextViewTotalPrice.setText(mStringValue);

        }

        // definir o escutador/ouvinte para o click no botão de adicionar quantidade
        public class ClickMyButtonAdd implements View.OnClickListener{
            @Override
            public void onClick(View v) {
                vQuantity = mProductList.get(getAdapterPosition()).getUnit() +1;
                if(  vQuantity >= 10  ){
                    mButtonProductQuantity.setTextSize(12f);
                } else {
                    mButtonProductQuantity.setTextSize(18f);
                }
                mProductList.get(getAdapterPosition()).setUnit(vQuantity);
                showTotalPrice(); //isso irá mudar

            }
        }

        // definir escutador/ouvinte do clique no botao de remover
        public class ClickMyButtonRemove implements View.OnClickListener{
            @Override
            public void onClick(View v) {
                vQuantity = mProductList.get(getAdapterPosition()).getUnit() -1;
                if( vQuantity >= 10  ){
                    mButtonProductQuantity.setTextSize(12f);
                } else {
                    mButtonProductQuantity.setTextSize(18f);
                }
                mProductList.get(getAdapterPosition()).setUnit(vQuantity);
                showTotalPrice();


            }
        }


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageViewProduct = itemView.findViewById(R.id.image_item_product_list_rev2);
            mTextViewProductName = itemView.findViewById(R.id.textView_item_product_name_rev2);
            mTextViewProductPrice = itemView.findViewById(R.id.textView_item_product_price_rev2);
            mRatingBarProduct = itemView.findViewById(R.id.ratingBar_item_product_price_perception_rev2);
            mButtonProductAdd = itemView.findViewById(R.id.button_product_add_rev2);
            mButtonProductRemove = itemView.findViewById(R.id.button_product_remove_rev2);
            mButtonProductQuantity = itemView.findViewById(R.id.button_product_quantity_rev2);

            // marcos fazer os listener dos botoes add e remove
            mButtonProductAdd.setOnClickListener(new ClickMyButtonAdd());
            mButtonProductRemove.setOnClickListener(new ClickMyButtonRemove());

            //configurar o click no item da minha lista
            itemView.setOnClickListener(mOnClickListener);
            itemView.setOnLongClickListener(mOnLongClickListener);



        }
    }



    // codigo para o recurso de busca/filtro/search
    private Filter applyProductFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
           List<Product> mFilteredList = new ArrayList<>(); //arraylist = lista dinamica
            // verificar a digitacao da busca
            if( constraint == null   ||  constraint.length() == 0  ) {
                mFilteredList.addAll(mProductListFull);
            }  else {
                // criar um objeto para padronizar a forma da busca
                String mFilterPattern = constraint.toString().toLowerCase().trim();
                // usar um laço de repetição para adicionar os produtos que
                // atendem a restricao CONSTRAINT da digitacao feita na busca
                for( Product mProduct : mProductList  ){
                    if(mProduct.getName().toLowerCase().trim().contains(constraint)){ // MARCOS EXPLICAR NOVAMENTE O LACO FOR e o IF
                        mFilteredList.add(mProduct);
                    }
                }

            }
            //objeto para representar a lista com o resultado
            FilterResults mFilterResults = new FilterResults();
            mFilterResults.values = mFilterResults;


            return mFilterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mProductList.clear();
            mProductList.addAll((List) results.values  );
            notifyDataSetChanged();
        }
    };


}
