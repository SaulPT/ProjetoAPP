package estg.psi.folclore.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import estg.psi.folclore.model.Evento;
import estg.psi.folclore.model.Grupo;
import estg.psi.folclore.model.Noticia;
import estg.psi.folclore.model.Parceria;

public class CacheDB extends SQLiteOpenHelper {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final SimpleDateFormat dateformat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());
    private static final int VERSAO = 12;
    private static final String NOME = "folclore.db";

    public CacheDB(Context context) {
        super(context, NOME, null, VERSAO);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS noticia" +
                " (id INT PRIMARY KEY," +
                " titulo TEXT," +
                " conteudo TEXT," +
                " data_criacao TEXT," +
                " data_edicao TEXT," +
                " autor_id INT," +
                " imagem TEXT," +
                " ativo INT," +
                " aprovado INT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS parceria" +
                " (id INT PRIMARY KEY," +
                " parceiro TEXT," +
                " site_parceiro TEXT," +
                " descricao TEXT," +
                " imagem TEXT," +
                " ativo INT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS evento" +
                " (id INT PRIMARY KEY," +
                " nome TEXT," +
                " descricao TEXT," +
                " local TEXT," +
                " data TEXT," +
                " data_criacao TEXT," +
                " concelho_id INT," +
                " autor_id INT," +
                " imagem TEXT," +
                " estado INT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS grupo" +
                " (id INT PRIMARY KEY," +
                " nome TEXT," +
                " abreviatura TEXT," +
                " concelho_id INT," +
                " logo TEXT," +
                " data_criacao TEXT," +
                " ativo INT)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS noticia");
            db.execSQL("DROP TABLE IF EXISTS parceria");
            db.execSQL("DROP TABLE IF EXISTS evento");
            db.execSQL("DROP TABLE IF EXISTS grupo");
            onCreate(db);
        }
    }


    public void inserir_noticias(List<Noticia> noticias) {
        //AO FUNCIONAR COMO CACHE, A BD APAGA TUDO
        getWritableDatabase().delete("noticia", null, null);

        //E INSERE OS DADOS QUE RECEBEU DA API
        for (Noticia noticia : noticias) {
            ContentValues valores = new ContentValues();
            valores.put("id", noticia.id);
            valores.put("titulo", noticia.titulo);
            valores.put("conteudo", noticia.conteudo);
            valores.put("data_criacao", dateformat.format(noticia.data_criacao));
            valores.put("data_edicao", dateformat.format(noticia.data_edicao));
            valores.put("autor_id", noticia.autor_id);
            valores.put("imagem", noticia.imagem);
            valores.put("ativo", noticia.ativo);
            valores.put("aprovado", noticia.aprovado);

            getWritableDatabase().insert("noticia", null, valores);
        }
    }

    public List<Noticia> obter_noticias() {
        Cursor query_cursor = getReadableDatabase().query("noticia", null, "ativo = 1", null, null, null, null);

        List<Noticia> noticias_list = new ArrayList<>();
        for (query_cursor.moveToFirst(); !query_cursor.isAfterLast(); query_cursor.moveToNext()) {
            Noticia noticia = new Noticia();
            noticia.id = query_cursor.getInt(query_cursor.getColumnIndex("id"));
            noticia.titulo = query_cursor.getString(query_cursor.getColumnIndex("titulo"));
            noticia.conteudo = query_cursor.getString(query_cursor.getColumnIndex("conteudo"));

            //O ANDROID OBRIGA TRATAR A EXCEPÇÃO QUE RESULTA DA CONVERSÃO DO TEXTO EM DATA
            try {
                String data = query_cursor.getString(query_cursor.getColumnIndex("data_criacao"));
                noticia.data_criacao = dateformat.parse(data);
                data = query_cursor.getString(query_cursor.getColumnIndex("data_edicao"));
                noticia.data_edicao = dateformat.parse(data);
            } catch (ParseException e) {
                Log.e("DateParse", e.getMessage());
            }

            noticia.autor_id = query_cursor.getInt(query_cursor.getColumnIndex("autor_id"));
            noticia.imagem = query_cursor.getString(query_cursor.getColumnIndex("imagem"));
            noticia.ativo = query_cursor.getInt(query_cursor.getColumnIndex("ativo"));
            noticia.aprovado = query_cursor.getInt(query_cursor.getColumnIndex("aprovado"));
            noticias_list.add(noticia);
        }
        query_cursor.close();

        //PARA ORDENAR POR ORDEM DECRESCENTE DA DATA
        Collections.sort(noticias_list, new Comparator<Noticia>() {
            @Override
            public int compare(Noticia n1, Noticia n2) {
                if (n1.data_edicao.after(n2.data_edicao)) {
                    return -1;
                } else if (n1.data_edicao.before(n2.data_edicao)) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        return noticias_list;
    }

    public void apagar_noticias() {
        getWritableDatabase().delete("noticia", null, null);
    }


    public void inserir_parcerias(List<Parceria> parcerias) {
        //AO FUNCIONAR COMO CACHE, A BD APAGA TUDO
        getWritableDatabase().delete("parceria", null, null);

        //E INSERE OS DADOS QUE RECEBEU DA API
        for (Parceria parceria : parcerias) {
            ContentValues valores = new ContentValues();
            valores.put("id", parceria.id);
            valores.put("parceiro", parceria.parceiro);
            valores.put("site_parceiro", parceria.site_parceiro);
            valores.put("descricao", parceria.descricao);
            valores.put("imagem", parceria.imagem);
            valores.put("ativo", parceria.ativo);

            getWritableDatabase().insert("parceria", null, valores);
        }
    }

    public List<Parceria> obter_parcerias() {
        Cursor query_cursor = getReadableDatabase().query("parceria", null, "ativo = 1", null, null, null, null);

        List<Parceria> parcerias_list = new ArrayList<>();
        for (query_cursor.moveToFirst(); !query_cursor.isAfterLast(); query_cursor.moveToNext()) {
            Parceria parceria = new Parceria();
            parceria.id = query_cursor.getInt(query_cursor.getColumnIndex("id"));
            parceria.parceiro = query_cursor.getString(query_cursor.getColumnIndex("parceiro"));
            parceria.site_parceiro = query_cursor.getString(query_cursor.getColumnIndex("site_parceiro"));
            parceria.descricao = query_cursor.getString(query_cursor.getColumnIndex("descricao"));
            parceria.imagem = query_cursor.getString(query_cursor.getColumnIndex("imagem"));
            parceria.ativo = query_cursor.getInt(query_cursor.getColumnIndex("ativo"));
            parcerias_list.add(parceria);
        }
        query_cursor.close();

        //PARA ORDENAR POR ORDEM ALFABÉTICA
        Collections.sort(parcerias_list, new Comparator<Parceria>() {
            @Override
            public int compare(Parceria p1, Parceria p2) {
                if (p1.parceiro.compareToIgnoreCase(p2.parceiro) < 0) {
                    return -1;
                } else if (p1.parceiro.compareToIgnoreCase(p2.parceiro) > 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        return parcerias_list;
    }

    public void apagar_parcerias() {
        getWritableDatabase().delete("parceria", null, null);
    }


    public void inserir_eventos(List<Evento> eventos) {
        //AO FUNCIONAR COMO CACHE, A BD APAGA TUDO
        getWritableDatabase().delete("evento", null, null);

        //E INSERE OS DADOS QUE RECEBEU DA API
        for (Evento evento : eventos) {
            ContentValues valores = new ContentValues();
            valores.put("id", evento.id);
            valores.put("nome", evento.nome);
            valores.put("descricao", evento.descricao);
            valores.put("local", evento.local);
            valores.put("data", dateformat.format(evento.data));
            valores.put("data_criacao", dateformat.format(evento.data_criacao));
            valores.put("concelho_id", evento.concelho_id);
            valores.put("autor_id", evento.autor_id);
            valores.put("imagem", evento.imagem);
            valores.put("estado", evento.estado);

            getWritableDatabase().insert("evento", null, valores);
        }
    }

    public List<Evento> obter_eventos() {
        Cursor query_cursor = getReadableDatabase().query("evento", null, "estado = 1", null, null, null, null);

        List<Evento> eventos_list = new ArrayList<>();
        for (query_cursor.moveToFirst(); !query_cursor.isAfterLast(); query_cursor.moveToNext()) {
            Evento evento = new Evento();
            evento.id = query_cursor.getInt(query_cursor.getColumnIndex("id"));
            evento.nome = query_cursor.getString(query_cursor.getColumnIndex("nome"));
            evento.descricao = query_cursor.getString(query_cursor.getColumnIndex("descricao"));
            evento.local = query_cursor.getString(query_cursor.getColumnIndex("local"));

            //O ANDROID OBRIGA TRATAR A EXCEPÇÃO QUE RESULTA DA CONVERSÃO DO TEXTO EM DATA
            try {
                String data = query_cursor.getString(query_cursor.getColumnIndex("data"));
                evento.data = dateformat.parse(data);
                data = query_cursor.getString(query_cursor.getColumnIndex("data_criacao"));
                evento.data_criacao = dateformat.parse(data);
            } catch (ParseException e) {
                Log.e("DateParse", e.getMessage());
            }

            evento.concelho_id = query_cursor.getInt(query_cursor.getColumnIndex("concelho_id"));
            evento.autor_id = query_cursor.getInt(query_cursor.getColumnIndex("autor_id"));
            evento.imagem = query_cursor.getString(query_cursor.getColumnIndex("imagem"));
            evento.estado = query_cursor.getInt(query_cursor.getColumnIndex("estado"));
            eventos_list.add(evento);
        }
        query_cursor.close();

        //PARA ORDENAR POR ORDEM CRESCENTE DA DATA
        Collections.sort(eventos_list, new Comparator<Evento>() {
            @Override
            public int compare(Evento e1, Evento e2) {
                if (e1.data.after(e2.data)) {
                    return 1;
                } else if (e1.data.before(e2.data)) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        return eventos_list;
    }

    public void apagar_eventos() {
        getWritableDatabase().delete("evento", null, null);
    }


    public void inserir_grupos(List<Grupo> grupos) {
        //AO FUNCIONAR COMO CACHE, A BD APAGA TUDO
        getWritableDatabase().delete("grupo", null, null);

        //E INSERE OS DADOS QUE RECEBEU DA API
        for (Grupo grupo : grupos) {
            ContentValues valores = new ContentValues();
            valores.put("id", grupo.id);
            valores.put("nome", grupo.nome);
            valores.put("abreviatura", grupo.abreviatura);
            valores.put("concelho_id", grupo.concelho_id);
            valores.put("logo", grupo.logo);
            valores.put("data_criacao", dateformat.format(grupo.data_criacao));
            valores.put("ativo", grupo.ativo);

            getWritableDatabase().insert("grupo", null, valores);
        }
    }

    public List<Grupo> obter_grupos() {
        Cursor query_cursor = getReadableDatabase().query("grupo", null, null, null, null, null, null);

        List<Grupo> grupos_list = new ArrayList<>();
        for (query_cursor.moveToFirst(); !query_cursor.isAfterLast(); query_cursor.moveToNext()) {
            Grupo grupo = new Grupo();
            grupo.id = query_cursor.getInt(query_cursor.getColumnIndex("id"));
            grupo.nome = query_cursor.getString(query_cursor.getColumnIndex("nome"));
            grupo.abreviatura = query_cursor.getString(query_cursor.getColumnIndex("abreviatura"));
            grupo.concelho_id = query_cursor.getInt(query_cursor.getColumnIndex("concelho_id"));
            grupo.logo = query_cursor.getString(query_cursor.getColumnIndex("logo"));
            grupo.ativo = query_cursor.getInt(query_cursor.getColumnIndex("ativo"));

            //O ANDROID OBRIGA TRATAR A EXCEPÇÃO QUE RESULTA DA CONVERSÃO DO TEXTO EM DATA
            try {
                String data = query_cursor.getString(query_cursor.getColumnIndex("data_criacao"));
                grupo.data_criacao = dateformat.parse(data);
            } catch (ParseException e) {
                Log.e("DateParse", e.getMessage());
            }

            grupos_list.add(grupo);
        }
        query_cursor.close();

        //PARA ORDENAR POR ORDEM ALFABÉTICA
        Collections.sort(grupos_list, new Comparator<Grupo>() {
            @Override
            public int compare(Grupo g1, Grupo g2) {
                if (g1.abreviatura.compareToIgnoreCase(g2.abreviatura) < 0) {
                    return -1;
                } else if (g1.abreviatura.compareToIgnoreCase(g2.abreviatura) > 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        return grupos_list;
    }

    public Grupo obter_grupo(int id) {
        Cursor query_cursor = getReadableDatabase().query("grupo", null, "id = " + id, null, null, null, null);
        Grupo grupo = null;

        if (query_cursor.getCount() > 0) {
            query_cursor.moveToFirst();
            grupo = new Grupo();
            grupo.id = query_cursor.getInt(query_cursor.getColumnIndex("id"));
            grupo.nome = query_cursor.getString(query_cursor.getColumnIndex("nome"));
            grupo.abreviatura = query_cursor.getString(query_cursor.getColumnIndex("abreviatura"));
            grupo.concelho_id = query_cursor.getInt(query_cursor.getColumnIndex("concelho_id"));
            grupo.logo = query_cursor.getString(query_cursor.getColumnIndex("logo"));
            grupo.ativo = query_cursor.getInt(query_cursor.getColumnIndex("ativo"));

            //O ANDROID OBRIGA TRATAR A EXCEPÇÃO QUE RESULTA DA CONVERSÃO DO TEXTO EM DATA
            try {
                String data = query_cursor.getString(query_cursor.getColumnIndex("data_criacao"));
                grupo.data_criacao = dateformat.parse(data);
            } catch (ParseException e) {
                Log.e("DateParse", e.getMessage());
            }
        }

        query_cursor.close();

        return grupo;
    }

    public void alterar_grupo(Grupo grupo) {
        ContentValues valores = new ContentValues();
        valores.put("id", grupo.id);
        valores.put("nome", grupo.nome);
        valores.put("abreviatura", grupo.abreviatura);
        valores.put("concelho_id", grupo.concelho_id);
        valores.put("logo", grupo.logo);
        valores.put("data_criacao", dateformat.format(grupo.data_criacao));
        valores.put("ativo", grupo.ativo);

        getWritableDatabase().update("grupo", valores, "id = " + grupo.id, null);
    }

    public void apagar_grupos() {
        getWritableDatabase().delete("grupo", null, null);
    }
}