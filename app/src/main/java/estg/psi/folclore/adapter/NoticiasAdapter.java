package estg.psi.folclore.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import estg.psi.folclore.database.CacheDB;
import estg.psi.folclore.model.Noticia;


public class NoticiasAdapter extends ArrayAdapter<Noticia> {

    private final Context context;
    private final List<Noticia> noticias;

    public NoticiasAdapter(Context context, int layout, List<Noticia> noticias) {
        super(context, layout, noticias);
        this.noticias = noticias;
        this.context = context;
    }

    @Override
    public
    @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Noticia noticia = noticias.get(position);

        return BaseAdapter.getview_generico(context, convertView, parent,
                noticia.titulo, CacheDB.dateformat.format(noticia.data_edicao), noticia.conteudo, noticia.imagem, "noticias/");
    }
}

