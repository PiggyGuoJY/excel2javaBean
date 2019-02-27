package com.tkp.tkpole.starter.utils.regex;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.tkp.tkpole.starter.utils.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.oro.text.regex.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.ExecutionException;

import static com.tkp.tkpole.starter.utils.Assert.notNul;
import static com.tkp.tkpole.starter.utils.Assert.notNull;

/**
 * 正则工具书写的抽象类
 * 所有正则工具要继承此类
 * 示例:<pre>
 * final class XXXRegex extends AbstractRegex {
 *     &#064;Override
 *     protected String setRegex() {
 *         //返回正则表达式
 *         return "......";
 *     }
 * }
 *
 * ...
 * boolean matched = new XXXRegex().match( xxx);
 * ...
 * </pre>
 *
 * <p> 创建时间：2018/7/31
 *
 * @author guojy24
 * @version 1.0
 *
 * */
@Slf4j
abstract public class AbstractRegex {
    protected AbstractRegex () {
        this.regex4Match = setRegex4Match();
        this.regex4Mask = setRegex4Mask();
        this.substitute4Mask = setSubstitute4Mask();
    }
    /**
     * <p> 设置正则表达式
     *
     * @return 描述返回值
     * */
    protected abstract String setRegex4Match();
    /**
     * <p> 设置正则表达式
     *
     * @return 描述返回值
     * */
    protected String setRegex4Mask() {
        return null;
    }
    /**
     * <p> 设置正则表达式
     *
     * @return 描述返回值
     * */
    protected String setSubstitute4Mask() {
        return null;
    }


    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param src 描述此参数的作用
     * @return 描述返回值
     * */
    final public boolean match( String src) {
        Pattern pattern = getPattern( this.regex4Match);
        return notNull( pattern) && notNul( src) && MATCHER.matches( src, pattern);
    }

    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param src 描述此参数的作用
     * @return 描述返回值
     * */
    final public String mask( String src) {
        return notNull( src)&& notNull( this.regex4Mask) && notNull( this.substitute4Mask) ? Util.substitute( new Perl5Matcher(), getPattern( this.regex4Mask), getPerl5Substitution( this.substitute4Mask), src, Util.SUBSTITUTE_ALL) : null ;
    }

    private static final PatternMatcher MATCHER = new Perl5Matcher();
    private String regex4Match;
    private String regex4Mask = "^(.*)$";
    private String substitute4Mask = "$1";
    private static final LoadingCache<String,Pattern> PATTERN_LOADING_CACHE =
            CacheBuilder
                    .newBuilder()
                    .initialCapacity(10)
                    .maximumSize(20)
                    .removalListener( notification -> log.info( "[{}]已从PATTERN_LOADING_CACHE中移除, 移除原因是[{}]", notification.getKey(), notification.getCause().name()))
                    .build( new CacheLoader<String,Pattern>() {
                        @Override @ParametersAreNonnullByDefault
                        public Pattern load(  String key) throws Exception { return new Perl5Compiler().compile( key, Perl5Compiler.READ_ONLY_MASK); }});
    private static final LoadingCache<String,Perl5Substitution> PERL_5_SUBSTITUTION_LOADING_CACHE =
            CacheBuilder
                    .newBuilder()
                    .initialCapacity(10)
                    .maximumSize(20)
                    .removalListener( notification -> log.info( "[{}]已从PERL_5_SUBSTITUTION_LOADING_CACHE中移除, 移除原因是[{}]", notification.getKey(), notification.getCause().name()))
                    .build( new CacheLoader<String,Perl5Substitution>() {
                        @Override @ParametersAreNonnullByDefault
                        public Perl5Substitution load(  String key) throws Exception { return new Perl5Substitution( key); }});
    private static Pattern getPattern( String regex) {
        try {
            return PATTERN_LOADING_CACHE.get( regex);
        } catch ( ExecutionException e) {
            log.error( e.getMessage(), e);
            return null;
        }
    }

    private static Perl5Substitution getPerl5Substitution ( String substitute) {
        try {
            return PERL_5_SUBSTITUTION_LOADING_CACHE.get( substitute);
        } catch ( ExecutionException e) {
            log.error( e.getMessage(), e);
            return null;
        }
    }
}
