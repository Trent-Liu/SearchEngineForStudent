package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.index.impl.TermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleScanner;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StringSplitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class TermTupleScanner extends AbstractTermTupleScanner {


    int pos=0; //单词当前位置

    /**
     * 构造函数
     *
     * @param input ：指定输入流对象，应该关联到一个文本文件
     */
    public TermTupleScanner(BufferedReader input) {
        super(input);
    }
    /**
     * 用于缓存每一行多余的数据
     */
    Queue<AbstractTermTuple> stringBuff = new LinkedList<>();

    /**
     * 获得下一个三元组
     *
     * @return: 下一个三元组；如果到了流的末尾，返回null
     */
    @Override
    public AbstractTermTuple next() {
        try {
            //目前的stringBuff还没空，指上一次读取的一行中的所有单词还没有全结束
            if (stringBuff.isEmpty()) {

                //读取新的一行字符
                String str = this.input.readLine();
                if (str == null) {
                    return null;
                }

                //新读取的一行为空行
                while (str.trim().length() == 0) {
                    str = input.readLine();
                    if (str == null) {
                        return null;
                    }
                }

                StringSplitter split = new StringSplitter();
                split.setSplitRegex(Config.STRING_SPLITTER_REGEX);
                for (String word : split.splitByRegex(str)) {
                    TermTuple tt = new TermTuple();
                    tt.curPos = pos;
                    // 是否忽略大小写
                    if (Config.IGNORE_CASE) {
                        tt.term = new Term(word.toLowerCase());
                    } else {
                        tt.term = new Term(word);
                    }
                    stringBuff.add(tt);
                    pos++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuff.poll();
    }
}