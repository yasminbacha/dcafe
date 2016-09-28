package epamig.dcafe.adaptadorCustomizado;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import epamig.dcafe.ListarDemarcacoesActivity;
import epamig.dcafe.R;
import epamig.dcafe.VisualizarDemarcacao;

public class CustomAdapterListaDemarcacoes extends BaseAdapter {
    int[] idDemarcacao;
    String[] classeOriginal;
    String[] classeDemarcada;
    String[] cidade;
    String[] comentario;
    String[] redesenho;

    Context context;
    private static LayoutInflater inflater = null;

    public CustomAdapterListaDemarcacoes(ListarDemarcacoesActivity demarcacoesActivity, int[] IdDemarcacaoList,
                                         String[] ClasseOriginalList,
                                         String[] ClasseDemarcadaList, String[] CidadeDemarcadaList,
                                         String[] ComentariosList, String[] RedesenhoList) {


        idDemarcacao = IdDemarcacaoList;
        classeOriginal = ClasseOriginalList;
        classeDemarcada = ClasseDemarcadaList;
        cidade = CidadeDemarcadaList;
        comentario = ComentariosList;
        redesenho = RedesenhoList;

        context = demarcacoesActivity;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return idDemarcacao.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView txtClasseOriginalPoligono;
        TextView txtCidadePoligono;
        TextView txtClasseDemarcadaPoligono;
        TextView txtComentariosPoligono;
        TextView txtPoligonoRedesenhado;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.custom_linear_layout, null);
        holder.txtClasseOriginalPoligono = (TextView) rowView.findViewById(R.id.txtClasseOriginalPoligonoVis);
        holder.txtClasseDemarcadaPoligono = (TextView) rowView.findViewById(R.id.txtClasseDemarcadaPoligonoVis);
        holder.txtCidadePoligono = (TextView) rowView.findViewById(R.id.txtCidadePoligono);
        holder.txtComentariosPoligono = (TextView) rowView.findViewById(R.id.txtComentariosPoligonoVis);
        holder.txtPoligonoRedesenhado = (TextView) rowView.findViewById(R.id.txtPoligonoRedesenhado);

        holder.txtClasseOriginalPoligono.setText(classeOriginal[position]);
        holder.txtClasseDemarcadaPoligono.setText(classeDemarcada[position]);
        holder.txtCidadePoligono.setText(cidade[position]);
        holder.txtComentariosPoligono.setText(comentario[position]);
        holder.txtPoligonoRedesenhado.setText(redesenho[position]);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(context, VisualizarDemarcacao.class);
                i.putExtra("idDemarcacao", idDemarcacao[position]);
                context.startActivity(i);
            }
        });
        return rowView;
    }
}