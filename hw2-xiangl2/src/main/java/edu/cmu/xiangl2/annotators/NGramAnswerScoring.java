package edu.cmu.xiangl2.annotators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

import edu.cmu.deiis.types.*;

/**
 * Analysis engine which assign a score to each answer to indicate how likely it could be a correct
 * answer.
 * <p>
 * The scoring method is based on N-Gram hit rate between sentence and answer. A high score shows
 * that the answer is likely to be a correct answer.
 */
public class NGramAnswerScoring extends JCasAnnotator_ImplBase {

  /**
   * The major scoring process.
   * <p>
   * This method assign a score to each answer to show how likely it could be a correct answer. The
   * score is calculated using N-Gram hit rate. That is to say, the system counts the number of
   * N-Gram annotations in the answer sentences that could be matched with N-Gram annotations in
   * the question sentence, then calculates the ratio of matched N-Gram annotation number to total
   * N-Gram annotation number.
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    FSIndex<?> questionIndex = aJCas.getAnnotationIndex(Question.type);
    Iterator<?> questionIter = questionIndex.iterator();
    List<NGram> questionNgram = null;
    if (questionIter.hasNext()) {
      questionNgram = getSentenceNgram(aJCas, (Annotation) questionIter.next());
    }

    FSIndex<?> answerIndex = aJCas.getAnnotationIndex(Answer.type);
    Iterator<?> answerIter = answerIndex.iterator();
    List<NGram> answerNgram = null;
    while (answerIter.hasNext()) {
      Answer answer = (Answer) answerIter.next();
      answerNgram = getSentenceNgram(aJCas, (Annotation) answer);
      int total = answerNgram.size();
      int match = 0;
      for (int i = 0; i < questionNgram.size(); i++) {
        for (int j = 0; j < answerNgram.size(); j++) {
          if (aJCas
                  .getDocumentText()
                  .substring(questionNgram.get(i).getBegin(), questionNgram.get(i).getEnd())
                  .equals(aJCas.getDocumentText().substring(answerNgram.get(j).getBegin(),
                          answerNgram.get(j).getEnd()))) {
            match += 1;
          }
        }
      }
      AnswerScore annotation = new AnswerScore(aJCas);
      annotation.setBegin(answer.getBegin());
      annotation.setEnd(answer.getEnd());
      annotation.setConfidence(1);
      annotation.setCasProcessorId(this.getClass().getName());
      annotation.setAnswer(answer);
      annotation.setScore(match / (double) total);
      annotation.addToIndexes();
    }
  }

  private List<NGram> getSentenceNgram(JCas aJCas, Annotation annotation) {
    List<NGram> result = new ArrayList<NGram>();
    FSIndex<?> ngramIndex = aJCas.getAnnotationIndex(NGram.type);
    Iterator<?> ngramIter = ngramIndex.iterator();
    int begin, end;
    begin = annotation.getBegin();
    end = annotation.getEnd();
    while (ngramIter.hasNext()) {
      NGram ngram = (NGram) ngramIter.next();
      if (ngram.getBegin() >= begin && ngram.getEnd() <= end) {
        result.add(ngram);
      }
    }
    return result;
  }

}
