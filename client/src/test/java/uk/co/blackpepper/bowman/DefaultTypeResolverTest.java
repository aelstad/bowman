package uk.co.blackpepper.bowman;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;

import uk.co.blackpepper.bowman.annotation.RemoteResource;
import uk.co.blackpepper.bowman.annotation.ResourceTypeInfo;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class DefaultTypeResolverTest {

	public static class TypeWithoutInfo {
		// no members
	}
	
	@ResourceTypeInfo
	public static class TypeWithUnderspecifiedInfo {
		// no members
	}
	
	@ResourceTypeInfo(subtypes = Object.class, typeResolver = Resolver.class)
	public static class TypeWithOverspecifiedInfo {
		// no members
	}
	
	@ResourceTypeInfo(subtypes = {TypeWithSubtypesSubtype1.class, TypeWithSubtypesSubtype2.class})
	public static class TypeWithSubtypes {
		// no members
	}
	
	@RemoteResource("/1")
	public static class TypeWithSubtypesSubtype1 {
		// no members
	}
	
	@RemoteResource("/2")
	public static class TypeWithSubtypesSubtype2 {
		// no members
	}
	
	@ResourceTypeInfo(typeResolver = Resolver.class)
	public static class TypeWithResolver {
		// no members
	}
	
	public static class Resolver implements TypeResolver {
		
		// CHECKSTYLE:OFF
		
		public static TypeResolver mockResolver = mock(TypeResolver.class);
		
		// CHECKSTYLE:ON
		
		@Override
		public Class<?> resolveType(Class<?> declaredType, Links resourceLinks, Configuration configuration) {
			return mockResolver.resolveType(declaredType, resourceLinks, configuration);
		}
	}
	
	public static class TypeWithResolverSubtype extends TypeWithResolver {
		// no members
	}
	
	private DefaultTypeResolver resolver;
	
	private ExpectedException thrown = ExpectedException.none();
	
	@Rule
	public ExpectedException getThrown() {
		return thrown;
	}
	
	@Before
	public void setUp() {
		resolver = new DefaultTypeResolver();
	}
	
	@Test
	public void resolveTypeWithNoResourceTypeInfoReturnsDeclaredType() {
		Class<?> type = resolver.resolveType(TypeWithoutInfo.class,
			new Links(new Link("http://x", Link.REL_SELF)), Configuration.build());
		
		assertThat(type, Matchers.<Class<?>>equalTo(TypeWithoutInfo.class));
	}
	
	@Test
	public void resolveTypeWithUnderspecifiedResourceTypeInfoThrowsException() {
		thrown.expect(IllegalStateException.class);
	
		resolver.resolveType(TypeWithUnderspecifiedInfo.class, new Links(), Configuration.build());
	}
	
	@Test
	public void resolveTypeWithOverspecifiedResourceTypeInfoThrowsException() {
		thrown.expect(IllegalStateException.class);
		
		resolver.resolveType(TypeWithOverspecifiedInfo.class, new Links(), Configuration.build());
	}
	
	@Test
	public void resolveTypeWithSubtypesAndNoSelfLinkReturnsDeclaredType() {
		Class<?> type = resolver.resolveType(TypeWithSubtypes.class, new Links(), Configuration.build());
		
		assertThat(type, Matchers.<Class<?>>equalTo(TypeWithSubtypes.class));
	}
	
	@Test
	public void resolveTypeWithSubtypesAndNoMatchingSubtypeReturnsDeclaredType() {
		Class<?> type = resolver.resolveType(TypeWithSubtypes.class,
			new Links(new Link("http://x", Link.REL_SELF)), Configuration.build());
		
		assertThat(type, Matchers.<Class<?>>equalTo(TypeWithSubtypes.class));
	}

	@Test
	public void resolveTypeWithSubtypesAndMatchingAbsoluteUriSubtypeReturnsSubtype() {
		Configuration config = Configuration.builder().setBaseUri("http://x.com").build();
		
		Class<?> type = resolver.resolveType(TypeWithSubtypes.class,
			new Links(new Link("http://x.com/2/1", Link.REL_SELF)), config);
		
		assertThat(type, Matchers.<Class<?>>equalTo(TypeWithSubtypesSubtype2.class));
	}
	
	@Test
	public void resolveTypeWithSubtypesAndMatchingAbsolutePathReferenceUriSubtypeReturnsSubtype() {
		Configuration config = Configuration.builder().setBaseUri("http://x.com").build();
		
		Class<?> type = resolver.resolveType(TypeWithSubtypes.class,
			new Links(new Link("/2/1", Link.REL_SELF)), config);
		
		assertThat(type, Matchers.<Class<?>>equalTo(TypeWithSubtypesSubtype2.class));
	}
	
	@Test
	public void resolveTypeWithResolverReturnsResolvedType() {
		Links links = new Links(new Link("_"));
		Configuration config = Configuration.build();
		
		doReturn(TypeWithResolverSubtype.class)
			.when(Resolver.mockResolver).resolveType(TypeWithResolver.class, links, config);
		
		Class<?> type = resolver.resolveType(TypeWithResolver.class, links, config);
		
		assertThat(type, Matchers.<Class<?>>equalTo(TypeWithResolverSubtype.class));
	}
}
