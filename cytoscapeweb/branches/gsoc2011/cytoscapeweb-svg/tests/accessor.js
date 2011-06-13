/**
 * Create an accessor function on the target object.
 * @param {object} targetObject Container object which stores the data fields
 * @param {array} immutableFields List of names of fields to protect from modification
 * @param {array} allowedFields Exlusive list of field names that are allowed for reading and (if not immutable) writing, or null if all fields are allowed.
 * @param {object} proxyFields Mapping of proxy field names to names of fields in the proxy object (optional)
 * @param {object} proxyObject Object onto which proxy fields are proxied, or null (optional)
 * @return {function} Accessor function
 */
function accessor(targetObject, immutableFields, allowedFields, proxyFields, proxyObject) {

	proxyFields = proxyFields || [];
	immutableFields = immutableFields || [];
	var accessor = function() {
	
		// f() --> return a copy of all fields
		if (arguments.length === 0) {
		
			// Produce a shallow copy
			var copy = {};
			for (var field in targetObject) copy[field] = targetObject[field];
			if (proxyObject) {
				for (var field in proxyFields) {
					copy[field] = proxyObject[proxyFields[field]];
				}
			}
			return copy;
			
		// f("fieldname") --> return field
		} else if (arguments.length == 1 && typeof arguments[0] == "string") {
		
			// Check if the field is allowed
			if (allowedFields && allowedFields.indexOf(arguments[0]) == -1) throw "Invalid field name '" + arguments[0] + "'";
		
			// Check if the field is proxied
			if (arguments[0] in proxyFields) return proxyObject[proxyFields[arguments[0]]];
			
			// Return the field from the target object
			return targetObject[arguments[0]];
			
		// f({field:value, ...}) --> update the target object with the given fields
		} else if (arguments.length == 1 && typeof arguments[0] == "object") {
		
			// Delegate the job to f(field, value) calls (in order to perform the necessary checks on each.)
			for (var field in arguments[0]) {
				accessor(field, arguments[0][field]);
			}
			
			return this; // fluent interface return
			
		// f("fieldname", value) --> update the field with the given value
		} else if (arguments.length == 2 && typeof arguments[0] == "string") {
		
			// Check if the field is allowed
			if (allowedFields && allowedFields.indexOf(arguments[0]) == -1) throw "Invalid field name '" + arguments[0] + "'";	
			
			// Check if the field is not immutable
			if (immutableFields && immutableFields.indexOf(arguments[0]) != -1) throw "Cannot modify immutable field '" + arguments[0] + "'";
			
			// Check if the field is proxied; if so, set the field on the proxied object
			if (arguments[0] in proxyFields) proxyObject[proxyFields[arguments[0]]] = arguments[1];
			
			// Set the field in the target object
			else targetObject[arguments[0]] = arguments[1];
			
			return this; // fluent interface return
		}
	};
	return accessor;
};
