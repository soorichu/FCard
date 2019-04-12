package co.soori.wordflash;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;


public class MainActivity extends AppCompatActivity {

    private String english[];
    private String korean[];

    private TextView English;
    private TextView Korean;

    private Button before;
    private Button memorized;

    private ImageButton cardopen;

    private int num;
    private int Max;
    private String wordfile;
//    private String language;

    TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wordfile = "test.xls";
//        language = "ENGLISH";
        num = 0;
        Excel(wordfile);

        English = (TextView) findViewById(R.id.english);
        Korean = (TextView) findViewById(R.id.korean);
        memorized = (Button) findViewById(R.id.memorized);
        before = (Button) findViewById(R.id.before);
        cardopen = (ImageButton) findViewById(R.id.card_open);


        English.setText(english[num]);
        Korean.setText("");

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i!=TextToSpeech.ERROR){
                    if(wordfile=="chinese_test.xls")
                        tts.setLanguage(Locale.CHINESE);
                    else
                        tts.setLanguage(Locale.ENGLISH);
                }
            }
        });

        cardopen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wordfile=="test.xls"){
                    wordfile = "chineses_test.xls";
                    tts.setLanguage(Locale.CHINESE);
                }else if(wordfile=="chinese_text.xls"){
                    wordfile = "test.xls";
                    tts.setLanguage(Locale.ENGLISH);
                }
                Toast.makeText(getApplicationContext(), "Change to "+wordfile, Toast.LENGTH_SHORT);

                Excel(wordfile);
                num = 0;
                English.setText(english[num]);
                Korean.setText("");
            }
        });

        memorized.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                for(int i=num; i<Max-1; i++){
                     english[i] = english[i+1];
                     korean[i] = korean[i+1];
                }
                Max--;
                English.setText(english[num]);
                Korean.setText("");

            }
        });

        before.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                num = (num-1)%Max;
                English.setText(english[num]);
                Korean.setText("");

            }
        });

        English.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                num = (num+1)%Max;
                English.setText(english[num]);
                Korean.setText("");
            }

        });

        Korean.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if(Korean.getText()=="") {
                    Korean.setText(korean[num]);
                }else{
                    Korean.setText("");
                }

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    ttsGreater21(english[num]);
                }else{
                    ttsUnder20(english[num]);
                }

            }
        });

    }

    public void Excel(String fileName) {

        Workbook workbook = null;
        Sheet sheet = null;
        try {
            InputStream inputStream = getBaseContext().getResources().getAssets().open(fileName);
            workbook = Workbook.getWorkbook(inputStream);
            sheet = workbook.getSheet(0);
            int MaxColumn = 2, RowStart = 0, RowEnd = sheet.getColumn(MaxColumn - 1).length -1, ColumnStart = 0, ColumnEnd = sheet.getRow(2).length - 1;

            Max = RowEnd;
            english = new String[200];
            korean = new String[200];

            for(int row = RowStart;row <= RowEnd;row++) {
                english[row] = sheet.getCell(ColumnStart, row).getContents();
                korean[row] = sheet.getCell(ColumnStart+1, row).getContents();

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } finally {
            workbook.close();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text){
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text){
        String utteranceId = this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }
}
