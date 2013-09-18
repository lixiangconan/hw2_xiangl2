package edu.cmu.xiangl2.annotators;

import java.io.StringReader;
import java.util.Iterator;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;

import edu.cmu.deiis.types.Question;
import edu.cmu.deiis.types.Answer;
import edu.cmu.deiis.types.Token;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.process.PTBTokenizer.PTBTokenizerFactory;
import edu.stanford.nlp.process.Tokenizer;

public class TokenAnnotator extends JCasAnnotator_ImplBase {

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIndex<?> questionIndex = aJCas.getAnnotationIndex(Question.type);
    Iterator<?> questionIter = questionIndex.iterator();
    while (questionIter.hasNext()) {
      Question annotation = (Question) questionIter.next();
      TokenizerFactory<Word> factory = PTBTokenizerFactory.newTokenizerFactory();
      Tokenizer<Word> tokenizer = factory.getTokenizer(new StringReader(aJCas.getDocumentText()
              .substring(annotation.getBegin()+2, annotation.getEnd()-2)));
      while(tokenizer.hasNext()){
        Word word = tokenizer.next();
        Token token = new Token(aJCas);
        token.setBegin(word.beginPosition() + annotation.getBegin()+2);
        token.setEnd(word.endPosition() + annotation.getBegin()+2);
        token.setConfidence(1);
        token.setCasProcessorId(this.getClass().getName());
        token.addToIndexes();
      }
    }
    
    FSIndex<?> answerIndex = aJCas.getAnnotationIndex(Answer.type);
    Iterator<?> answerIter = answerIndex.iterator();
    while (answerIter.hasNext()) {
      Answer annotation = (Answer) answerIter.next();
      TokenizerFactory<Word> factory = PTBTokenizerFactory.newTokenizerFactory();
      Tokenizer<Word> tokenizer = factory.getTokenizer(new StringReader(aJCas.getDocumentText()
              .substring(annotation.getBegin()+4, annotation.getEnd()-2)));
      while(tokenizer.hasNext()){
        Word word = tokenizer.next();
        Token token = new Token(aJCas);
        token.setBegin(word.beginPosition() + annotation.getBegin()+4);
        token.setEnd(word.endPosition() + annotation.getBegin()+4);
        token.setConfidence(1);
        token.setCasProcessorId(this.getClass().getName());
        token.addToIndexes();
      }
    } 
  }
}
