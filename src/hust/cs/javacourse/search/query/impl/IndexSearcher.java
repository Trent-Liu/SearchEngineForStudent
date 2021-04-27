package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractPostingList;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.index.impl.Index;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;
import hust.cs.javacourse.search.util.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class IndexSearcher extends AbstractIndexSearcher {


    /**
     * 从指定索引文件打开索引，加载到index对象里. 一定要先打开索引，才能执行search方法
     *
     * @param indexFile ：指定索引文件
     */
    @Override
    public void open(String indexFile) {
        this.index = new Index();
        index.load(new File(indexFile));
    }




    /**
     * 根据单个检索词进行搜索
     *
     * @param queryTerm ：检索词
     * @param sorter    ：排序器
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm, Sort sorter) {

        AbstractPostingList indexSearchResult = index.search(queryTerm);
        List<AbstractHit> result = new ArrayList<>();

        if (indexSearchResult == null) {
            return null;
        }

        for (int i = 0; i < indexSearchResult.size(); i++) {
            AbstractPosting posting = indexSearchResult.get(i);

            HashMap<AbstractTerm,AbstractPosting> map = new HashMap<>();
            map.put(queryTerm,posting);
            //创建Hit
            AbstractHit hit = new Hit(posting.getDocId(), index.getDocName(posting.getDocId()),map);

            //获得此Hit的score
            hit.setScore(sorter.score(hit));

            //创建Hit列表
            result.add(hit);
        }

        sorter.sort(result);
        AbstractHit[] returnResult = new AbstractHit[result.size()];
        return result.toArray(returnResult);
    }

    /**
     * 根据二个检索词进行搜索
     *
     * @param queryTerm1 ：第1个检索词
     * @param queryTerm2 ：第2个检索词
     * @param sorter     ：    排序器
     * @param combine    ：   多个检索词的逻辑组合方式
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2, Sort sorter, AbstractIndexSearcher.LogicalCombination combine) {
        AbstractPostingList indexSearchResult1 = index.search(queryTerm1);
        AbstractPostingList indexSearchResult2 = index.search(queryTerm2);

        List<AbstractHit> result = new ArrayList<>();

        // 如果两个都没找到直接就是空的数组

        if (indexSearchResult1 == null && indexSearchResult2 == null) {
            return null;
        }


        if(combine==LogicalCombination.AND){
            if (indexSearchResult1 == null || indexSearchResult2 == null) {
                return null;
            }
            else
            {


                for (int i = 0; i < indexSearchResult1.size(); i++) {
                    AbstractPosting posting1 = indexSearchResult1.get(i);
                    int pos=indexSearchResult2.indexOf(posting1.getDocId());
                    AbstractPosting posting2=null;
                    if(pos!=-1) {
                        posting2=indexSearchResult2.get(pos);
                    }
                    if(posting2!=null)
                    {
                    HashMap<AbstractTerm,AbstractPosting> map = new HashMap<>();
                    map.put(queryTerm1,posting1);
                    map.put(queryTerm2,posting2);
                    //创建Hit
                    AbstractHit hit = new Hit(posting1.getDocId(), index.getDocName(posting1.getDocId()),map);

                    //获得此Hit的score
                    hit.setScore(sorter.score(hit));

                    //创建Hit列表
                    result.add(hit);
                    }
                }

                sorter.sort(result);
                return result.toArray(new AbstractHit[result.size()]);



            }

        }

        if(combine==LogicalCombination.OR){
            //如果有一个为空，对于OR来说，只搜索那个不为空的词
            if (indexSearchResult1 == null || indexSearchResult2 == null) {
                if(indexSearchResult1 == null) {
                    return search(queryTerm1,sorter);
                }
                if(indexSearchResult2 == null) {
                    return search(queryTerm2,sorter);
                }
            }
            //两个Term都不为空
            else{
                AbstractHit[] Trem1 = search(queryTerm1, sorter);
                AbstractHit[] Trem2 = search(queryTerm2, sorter);

                List<AbstractHit> result1=new ArrayList(Arrays.asList(Trem1));
                List<AbstractHit> result2=new ArrayList(Arrays.asList(Trem2));
                int result1Length=result1.size();
                for(AbstractHit hit:result2){
                    for(int i=0;i<result1Length;i++){
                        if(result1.get(i).getDocId()==hit.getDocId()){
                            result1.get(i).getTermPostingMapping().putAll(hit.getTermPostingMapping());
                            result1.get(i).setScore(sorter.score(result1.get(i)));
                            break;
                        }
                        if(i==result1.size()-1){
                            result1.add(hit);
                        }
                    }

                }


                sorter.sort(result1);
                AbstractHit[] returnResult = new AbstractHit[result1.size()];
                return result1.toArray(returnResult);
            }
        }

        return null;
    }
}
