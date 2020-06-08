package com.example.retrofitexample;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Interceptor;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView textViewResult;

    private JsonPlaceHolderApi jsonPlaceHolderApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewResult = findViewById(R.id.textViewResult);

        Gson gson = new GsonBuilder().serializeNulls().create();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public okhttp3.Response intercept(@NotNull Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        Request newRequest = originalRequest.newBuilder()
                                .header("Interceptor-Header","xyz")
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        //getPosts();
        //getComments();
        //createPost();
        //updatePost();
        //deletePost();
    }

    private void deletePost() {
        Date date = new Date();
        Call<Void> call = jsonPlaceHolderApi.deletePost(5);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                textViewResult.setText("Code: " + response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                textViewResult.setText(t.getMessage() + " " + date.toString());
                deletePost();
            }
        });
    }

    private void updatePost() {
        Date date = new Date();
        Post post = new Post(12,null,"New Text");

        Map<String,String> headers = new HashMap<>();
        headers.put("Map-Header1","abc");
        headers.put("Map-Header2","def");

        Call<Post> call = jsonPlaceHolderApi.patchPost(headers,5,post);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(!response.isSuccessful()){
                    textViewResult.setText("Code: " + response.code());
                    return;
                }

                Post postResponse = response.body();

                String content = "";
                content += "Code: " + response.code() +
                        "\nID: " + postResponse.getId() +
                        "\nUser Id: " + postResponse.getUserId() +
                        "\nTitle: " + postResponse.getTitle() +
                        "\nText: " + postResponse.getText() + "\n\n";
                textViewResult.setText(content);
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                textViewResult.setText(t.getMessage() + " " + date.toString());
                updatePost();
            }
        });
    }

    private void createPost() {
        Date date = new Date();
        Post post = new Post(23,"New Title","New Text");

        Map<String,String> fields = new HashMap<>();
        fields.put("userId", "25");
        fields.put("title","New Title");

        Call<Post> call = jsonPlaceHolderApi.createPost(fields);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(!response.isSuccessful()){
                    textViewResult.setText("Code: " + response.code());
                    return;
                }

                Post postResponse = response.body();

                String content = "";
                content += "Code: " + response.code() +
                        "\nID: " + postResponse.getId() +
                        "\nUser Id: " + postResponse.getUserId() +
                        "\nTitle: " + postResponse.getTitle() +
                        "\nText: " + postResponse.getText() + "\n\n";
                textViewResult.setText(content);
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                textViewResult.setText(t.getMessage() + " " + date.toString());
                createPost();
            }
        });
    }

    private void getPosts() {
        Date date = new Date();
        Map<String,String> parameters = new HashMap<>();
        parameters.put("userId","1");
        parameters.put("_sort","id");
        parameters.put("_order","desc");

        Call<List<Post>> call = jsonPlaceHolderApi.getPost(parameters);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {

                if(!response.isSuccessful()){
                    textViewResult.setText("Code: " + response.code());
                    return;
                }

                List<Post> posts = response.body();
                textViewResult.setText("");

                for(Post post : posts){
                    String content = "";
                    content += "ID: " + post.getId() +
                            "\nUser Id: " + post.getUserId() +
                            "\nTitle: " + post.getTitle() +
                            "\nText: " + post.getText() + "\n\n";
                    textViewResult.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                textViewResult.setText(t.getMessage()+date.toString());
                getPosts();
            }
        });
    }

    private void getComments() {
        Date date = new Date();
        Call<List<Comment>> call = jsonPlaceHolderApi.getComments("posts/3/comments");

        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if(!response.isSuccessful()){
                    textViewResult.setText("Code: " + response.code());
                    return;
                }

                List<Comment> comments = response.body();

                textViewResult.setText("");

                for(Comment comment : comments){
                    String content = "";
                    content += "ID: " + comment.getId() +
                            "\nPost ID: " + comment.getPostID() +
                            "\nName: " + comment.getName() +
                            "\nEmail: " + comment.getEmail() +
                            "\nText: " + comment.getText() + "\n\n";
                    textViewResult.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                textViewResult.setText(t.getMessage()+" "+date.toString());
                getComments();
            }
        });
    }
}
