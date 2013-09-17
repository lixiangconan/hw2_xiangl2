package edu.cmu.xiangl2.annotators;

import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import edu.cmu.deiis.types.Question;
import edu.cmu.deiis.types.Answer;

public class TextElementAnnotator extends JCasAnnotator_ImplBase {
  
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    String docText = aJCas.getDocumentText();
    docText.replace("\r\n", "\n");
    int start = 0;
    for(int i = 0; i < docText.length(); i++){
      if(docText.charAt(i) == '\n'){
        int end = i;
        if(docText.charAt(start) == 'Q'){
          Question annotation = new Question(aJCas);
          annotation.setBegin(start);
          annotation.setEnd(end);
          annotation.setConfidence(1);
          annotation.setCasProcessorId(this.getClass().getName());
          annotation.addToIndexes();
        }
        else if(docText.charAt(start) == 'A'){
          Answer annotation = new Answer(aJCas);
          annotation.setBegin(start);
          annotation.setEnd(end);
          annotation.setConfidence(1);
          annotation.setCasProcessorId(this.getClass().getName());
          if(docText.charAt(start+2) == '1'){
            annotation.setIsCorrect(true);
          }
          else{
            annotation.setIsCorrect(false);
          }
          annotation.addToIndexes();
        }
        start = i + 1;
      }
    }
  }

}
