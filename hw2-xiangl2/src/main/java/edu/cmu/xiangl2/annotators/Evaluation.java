package edu.cmu.xiangl2.annotators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import edu.cmu.deiis.types.Answer;
import edu.cmu.deiis.types.AnswerScore;

/**
 * This class outputs answers to the question and measures the performance of the system.
 * <p>
 * The Evaluator choose N answers with the highest scores as the answers given by the system, then
 * calculate the precision according to how many answers given by the system is correct.
 */
public class Evaluation extends JCasAnnotator_ImplBase {

  /**
   * The major evaluation process.
   * <p>
   * This method firstly order the answers according to their score. Then, N sentences with the
   * highest scores are chosen as the output answers. Finally, it counts how many chosen answers are
   * actually correct, and calculates a precision to measure the system performance.
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    int N = getCorrectNumber(aJCas);
    System.out.println("*******************************");
    System.out.println("N=" + N);
    System.out.println("*******************************");
    List<AnswerScore> correctAnswer = null;
    correctAnswer = chooseCorrectAnswer(aJCas, N);
    System.out.println("Answers given by the system:");
    for (int i = 0; i < correctAnswer.size(); i++) {
      System.out.println(aJCas.getDocumentText().substring(correctAnswer.get(i).getBegin(),
              correctAnswer.get(i).getEnd())
              + "Score: " + correctAnswer.get(i).getScore());
    }
    System.out.println("*******************************");
    int correctAnswerNumber = 0;
    double precision;
    for (int i = 0; i < correctAnswer.size(); i++) {
      if (correctAnswer.get(i).getAnswer().getIsCorrect())
        correctAnswerNumber = correctAnswerNumber + 1;
    }
    precision = correctAnswerNumber / (double) N;
    System.out.println("Precision: " + precision);
    System.out.println("*******************************");
  }

  /**
   * This method could count the number of correct answers according to the input information.
   * 
   * @param aJCas
   * @return
   */
  private int getCorrectNumber(JCas aJCas) {
    FSIndex<?> answerIndex = aJCas.getAnnotationIndex(Answer.type);
    Iterator<?> answerIter = answerIndex.iterator();
    int N = 0;
    while (answerIter.hasNext()) {
      Answer answer = (Answer) answerIter.next();
      if (answer.getIsCorrect())
        N = N + 1;
    }
    return N;
  }

  private class ComparatorAnswerScore implements Comparator {

    public int compare(Object arg0, Object arg1) {
      AnswerScore answerScore0 = (AnswerScore) arg0;
      AnswerScore answerScore1 = (AnswerScore) arg1;
      double delta = answerScore0.getScore() - answerScore1.getScore();
      if (delta > 0.00001)
        return 1;
      if (delta < -0.00001)
        return -1;
      return 0;
    }
  }

  /*
   * This method find n answers with the highest score.
   */
  private List<AnswerScore> chooseCorrectAnswer(JCas aJCas, int correctNuber) {
    List<AnswerScore> result = new ArrayList<AnswerScore>();
    List<AnswerScore> answerScoreList = new ArrayList<AnswerScore>();

    FSIndex<?> answerScoreIndex = aJCas.getAnnotationIndex(AnswerScore.type);
    Iterator<?> answerScoreIter = answerScoreIndex.iterator();

    while (answerScoreIter.hasNext()) {
      AnswerScore answerScore = (AnswerScore) answerScoreIter.next();
      answerScoreList.add(answerScore);
    }

    ComparatorAnswerScore comparator = new ComparatorAnswerScore();
    Collections.sort(answerScoreList, comparator);
    Collections.reverse(answerScoreList);
    for (int i = 0; i < correctNuber && i < answerScoreList.size(); i++) {
      result.add(answerScoreList.get(i));
    }
    return result;
  }
}
