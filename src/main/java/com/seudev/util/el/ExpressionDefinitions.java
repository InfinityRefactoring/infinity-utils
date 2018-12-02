package com.seudev.util.el;

import java.util.SortedMap;
import java.util.SortedSet;

import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CacheResult;
import javax.enterprise.context.ApplicationScoped;

/**
 * @author Thomás Sousa Silva (ThomasSousa96)
 */
public class ExpressionDefinitions {

	public static CurlyBracket ofCurlyBracket() {
		return CurlyBracket.INSTANCE;
	}

	public static DollarCurlyBracket ofDollarCurlyBracket() {
		return DollarCurlyBracket.INSTANCE;
	}

	public static DoubleQuoteLiteral ofDoubleQuoteLiteral() {
		return DoubleQuoteLiteral.INSTANCE;
	}

	public static SingleQuoteLiteral ofSingleQuoteLiteral() {
		return SingleQuoteLiteral.INSTANCE;
	}

	public static SquareBracket ofSquareBracket() {
		return SquareBracket.INSTANCE;
	}

	@ApplicationScoped
	@CacheDefaults(cacheName = "expression-definition-cache-curly-bracket")
	public static class CurlyBracket extends ExpressionDefinition {

		private static final long serialVersionUID = 3041374675306522624L;

		static final CurlyBracket INSTANCE = new CurlyBracket();

		public CurlyBracket() {
			super("{", "}", '\\', true, true);
		}

		@Override
		@CacheResult
		public SortedMap<Expression, SortedSet<Integer>> findAll(@CacheKey String template, @CacheKey int offset) {
			return super.findAll(template, offset);
		}

	}

	@ApplicationScoped
	@CacheDefaults(cacheName = "expression-definition-cache-dollar-curly-bracket")
	public static class DollarCurlyBracket extends ExpressionDefinition {

		private static final long serialVersionUID = 5728587168277276492L;

		static final DollarCurlyBracket INSTANCE = new DollarCurlyBracket();

		public DollarCurlyBracket() {
			super("${", "}", '\\', true, true);
		}

		@Override
		@CacheResult
		public SortedMap<Expression, SortedSet<Integer>> findAll(@CacheKey String template, @CacheKey int offset) {
			return super.findAll(template, offset);
		}

	}

	@ApplicationScoped
	@CacheDefaults(cacheName = "expression-definition-cache-double-quote-literal")
	public static class DoubleQuoteLiteral extends ExpressionDefinition {

		private static final long serialVersionUID = -4061392465572446514L;

		static final DoubleQuoteLiteral INSTANCE = new DoubleQuoteLiteral();

		public DoubleQuoteLiteral() {
			super("\"", "\"", '\\', true, false);
		}

		@Override
		@CacheResult
		public SortedMap<Expression, SortedSet<Integer>> findAll(@CacheKey String template, @CacheKey int offset) {
			return super.findAll(template, offset);
		}

	}

	@ApplicationScoped
	@CacheDefaults(cacheName = "expression-definition-cache-single-quote-literal")
	public static class SingleQuoteLiteral extends ExpressionDefinition {

		private static final long serialVersionUID = 3910252321268734650L;

		static final SingleQuoteLiteral INSTANCE = new SingleQuoteLiteral();

		public SingleQuoteLiteral() {
			super("'", "'", '\\', false, true);
		}

		@Override
		@CacheResult
		public SortedMap<Expression, SortedSet<Integer>> findAll(@CacheKey String template, @CacheKey int offset) {
			return super.findAll(template, offset);
		}

	}

	@ApplicationScoped
	@CacheDefaults(cacheName = "expression-definition-cache-square-bracket")
	public static class SquareBracket extends ExpressionDefinition {

		private static final long serialVersionUID = -360952570113673509L;

		static final SquareBracket INSTANCE = new SquareBracket();

		public SquareBracket() {
			super("[", "]", '\\', true, true);
		}

		@Override
		@CacheResult
		public SortedMap<Expression, SortedSet<Integer>> findAll(@CacheKey String template, @CacheKey int offset) {
			return super.findAll(template, offset);
		}

	}

}
