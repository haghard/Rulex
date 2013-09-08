
println " list size ${list.size()} -- ${value}"

result = list.find( { e -> e.getInteger() == value  } ) 

if (result)
  output = true
else
  output = false
  println "Script result: ${output}" 