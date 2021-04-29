package hust.cs.javacourse.search.run;

import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;
import hust.cs.javacourse.search.query.impl.IndexSearcher;
import hust.cs.javacourse.search.query.impl.SimpleSorter;
import hust.cs.javacourse.search.util.Config;

import javax.swing.plaf.nimbus.AbstractRegionPainter;
import java.util.Scanner;

/**
 * 测试搜索
 */
public class TestSearchIndex {
    public static void main(String[] args){
        Sort simpleSorter = new SimpleSorter();
        String indexFile = Config.INDEX_DIR + "index.dat";
        AbstractIndexSearcher searcher = new IndexSearcher();
        searcher.open(indexFile);
        String term1,term2,log;
        AbstractHit[] hits;
        Scanner scanner =new Scanner(System.in);
        int choice=1;


        while(choice!=0){
        System.out.println("\nPlease select the mode :\n1.one term\n2.two terms\n3.Exit");

        choice=scanner.nextInt();
            if(choice==3){
                break;
            }
            if(choice!=1&&choice!=2){
                System.out.println("Please input the right choice(1_3)");
                continue;
            }
        switch (choice) {
            case 1:
                System.out.println("Please input a term");
                term1=scanner.next();
                hits = searcher.search(new Term(term1), simpleSorter);
                if(hits==null){
                    System.out.println("No Doc!");
                }
                else{
                    for(AbstractHit hit : hits){
                        System.out.println(hit);}
                }
                break;

            case 2:
                System.out.println("Please input two terms and LogicalCombination.");
                System.out.println("First term");
                term1=scanner.next();
                System.out.println("Second term");
                term2=scanner.next();
                System.out.println("LogicalCombination");
                log=scanner.next();
                if(log.equals("AND")||log.equals("and")) {
                    hits = searcher.search(new Term(term1), new Term(term2), simpleSorter, AbstractIndexSearcher.LogicalCombination.AND);
                    if(hits==null){
                        System.out.println("No Doc!");
                    }
                    else{
                        for(AbstractHit hit : hits){
                            System.out.println(hit);}
                    }
                }

                else if(log.equals("OR")||log.equals("or")){
                    hits = searcher.search(new Term(term1), new Term(term2), simpleSorter, AbstractIndexSearcher.LogicalCombination.OR);
                    if(hits==null){
                        System.out.println("No Doc!");
                    }
                    else{
                        for(AbstractHit hit : hits){
                            System.out.println(hit);}
                    }
                }
                else
                {
                    System.out.println("LogicalCombination ERROR");
                }
                break;
            default:break;
        }

        }


    }
}
