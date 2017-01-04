package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.objectweb.asm.Type;

/**
 * A factory method utility for type model
 * 
 * @author zhang
 *
 */
class TypeParser {

	/**
	 * convert asm's type instance to TypeModel, assume this type has empty
	 * generic list
	 *
	 * @param type
	 * @return the corresponding type model
	 */
	static TypeModel parse(Type type) {
		switch (type.getSort()) {
		case Type.ARRAY:
			int dimension = type.getDimensions();
			type = type.getElementType();
			return new ArrayTypeModel(parse(type), dimension);
		case Type.OBJECT:
			ClassModel classModel = ASMParser.getClassByName(type.getClassName());
			return new ConcreteClassTypeModel(classModel, Collections.EMPTY_LIST);
		case Type.VOID:
			return PrimitiveType.VOID;
		case Type.INT:
			return PrimitiveType.INT;
		case Type.DOUBLE:
			return PrimitiveType.DOUBLE;
		case Type.CHAR:
			return PrimitiveType.CHAR;
		case Type.BOOLEAN:
			return PrimitiveType.BOOLEAN;
		case Type.LONG:
			return PrimitiveType.LONG;
		case Type.BYTE:
			return PrimitiveType.BYTE;
		case Type.SHORT:
			return PrimitiveType.SHORT;
		case Type.FLOAT:
			return PrimitiveType.FLOAT;
		default:
			throw new RuntimeException("does not suport type sort " + type.getClassName());
		}
	}

	/**
	 * get the type model for a specific class
	 *
	 * @param classModel
	 * @return
	 */
	static ConcreteClassTypeModel getType(ClassModel classModel) {
		return new ConcreteClassTypeModel(classModel, Collections.EMPTY_LIST);
	}

	/**
	 * get the type model for a generic class given its generic list
	 *
	 * @param classModel
	 * @return
	 */
	static ConcreteClassTypeModel getGenericType(ClassModel classModel, List<ConcreteClassTypeModel> genericList) {
		return new ConcreteClassTypeModel(classModel, genericList);
	}

	/**
	 * get the array type model for a specific class
	 *
	 * @param classModel
	 * @param dimension
	 *            the dimension of the array
	 * @return
	 */
	static TypeModel getArrayType(ClassModel classModel, int dimension) {
		return new ArrayTypeModel(getType(classModel), dimension);
	}

	/**
	 * 
	 * @param internalName
	 *            the internal name representing a type of a class
	 * @return the corresponding class type model
	 */
	static ClassTypeModel parseClassTypeModel(String internalName) {
		ClassModel bound = ASMParser.getClassByName(internalName.substring(1));
		ConcreteClassTypeModel type = TypeParser.getType(bound);
		return type;
	}

	/**
	 * 
	 * @param arg
	 *            the argument description string found in class or method's
	 *            signature
	 * @return the generic type model representing this
	 */
	static GenericTypeModel parseGenericType(String arg) {
		String[] sp = arg.split(":");
		String key = sp[0];
		ClassTypeModel type = parseClassTypeModel(sp[sp.length - 1]);
		return GenericTypeModel.getLowerBounded(type, key);
	}

	/**
	 * 
	 * @param signature
	 *            of a class or a method
	 * @return the list of generic parameter this class or method needs
	 */
	static List<GenericTypeModel> parseGenericTypeList(String signature) {
		List<GenericTypeModel> ls = new ArrayList<>();
		if (signature != null && signature.length() >= 1 && signature.charAt(0) == '<') {
			int count = 1, i = 1, j = 1;
			while (count != 0) {
				switch (signature.charAt(j)) {
				case '<':
					count++;
					break;
				case '>':
					count--;
					break;
				case ';':
					if (count == 1) {
						ls.add(TypeParser.parseGenericType(signature.substring(i, j)));
						i = j + 1;
					}
					break;
				default:
					break;
				}
				j++;
			}
		}
		return ls;
	}

}
