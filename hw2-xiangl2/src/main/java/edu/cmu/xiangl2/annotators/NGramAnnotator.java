package edu.cmu.xiangl2.annotators;

import java.util.Iterator;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import edu.cmu.deiis.types.Token;
import edu.cmu.deiis.types.NGram;

public class NGramAnnotator extends JCasAnnotator_ImplBase {

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {    
    process1Gram(aJCas);
    process2Gram(aJCas);
    process3Gram(aJCas);
  }
  private void process1Gram(JCas aJCas) throws AnalysisEngineProcessException {
    FSIndex<?> tokenIndex = aJCas.getAnnotationIndex(Token.type);
    Iterator<?> tokenIter = tokenIndex.iterator();
    while(tokenIter.hasNext()){
      Token token = (Token)tokenIter.next();
      NGram ngram = new NGram(aJCas);
      ngram.setBegin(token.getBegin());
      ngram.setEnd(token.getEnd());
      ngram.setCasProcessorId(this.getClass().getName());
      ngram.setConfidence(1);
      ngram.setElementType(token.getClass().getName());
      ngram.setElements(new FSArray(aJCas, 1));
      ngram.setElements(0, token);
      ngram.addToIndexes();
    }
  }
  public void process2Gram(JCas aJCas) throws AnalysisEngineProcessException {
    FSIndex<?> tokenIndex = aJCas.getAnnotationIndex(Token.type);
    Iterator<?> tokenIter = tokenIndex.iterator();
    Token token1, token2;
    
    if(tokenIter.hasNext())
      token1 = (Token) tokenIter.next();
    else
      return;
    
    while(tokenIter.hasNext()){
      token2 = (Token)tokenIter.next();
      int start, end;
      boolean flag = true;
      start = token1.getEnd() + 1;
      end = token2.getBegin() - 1;
      for(int i=start; i<=end; i++){
        if(aJCas.getDocumentText().charAt(i) == '\n')        {
          flag = false;
          break;
        }
      }
      if(flag){
        NGram ngram = new NGram(aJCas);
        ngram.setBegin(token1.getBegin());
        ngram.setEnd(token2.getEnd());
        ngram.setCasProcessorId(this.getClass().getName());
        ngram.setConfidence(1);
        ngram.setElementType(token1.getClass().getName());
        ngram.setElements(new FSArray(aJCas, 2));
        ngram.setElements(0, token1);
        ngram.setElements(1, token2);
        ngram.addToIndexes();
      }
      token1 = token2;
    }
  }
  public void process3Gram(JCas aJCas) throws AnalysisEngineProcessException {
    FSIndex<?> tokenIndex = aJCas.getAnnotationIndex(Token.type);
    Iterator<?> tokenIter = tokenIndex.iterator();
    Token token1, token2, token3;
    
    if(tokenIter.hasNext())
      token1 = (Token) tokenIter.next();
    else
      return;
    if(tokenIter.hasNext())
      token2 = (Token) tokenIter.next();
    else
      return;
    
    while(tokenIter.hasNext()){
      token3 = (Token)tokenIter.next();
      int start, end;
      boolean flag = true;
      start = token1.getEnd() + 1;
      end = token2.getBegin() - 1;
      for(int i=start; i<=end; i++){
        if(aJCas.getDocumentText().charAt(i) == '\n')        {
          flag = false;
          break;
        }
      }
      start = token2.getEnd() + 1;
      end = token3.getBegin() - 1;
      for(int i=start; i<=end; i++){
        if(aJCas.getDocumentText().charAt(i) == '\n')        {
          flag = false;
          break;
        }
      }
      if(flag){
        NGram ngram = new NGram(aJCas);
        ngram.setBegin(token1.getBegin());
        ngram.setEnd(token3.getEnd());
        ngram.setCasProcessorId(this.getClass().getName());
        ngram.setConfidence(1);
        ngram.setElementType(token1.getClass().getName());
        ngram.setElements(new FSArray(aJCas, 3));
        ngram.setElements(0, token1);
        ngram.setElements(1, token2);
        ngram.setElements(2, token3);
        ngram.addToIndexes();
      }
      token1 = token2;
      token2 = token3;
    }
  }

}