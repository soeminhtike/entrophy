
var simple_chart_config = {
	chart: {
		container: "#OrganiseChart-simple",
		connectors : {type:"step"}
	},
	
	nodeStructure: {
text:{name:"Repeat",criteria:"entry"}, children:[{text:{name:"Attendance",criteria:"1"}, children:[{text:{name:"Q18",criteria:"1"}, children:[{text:{name:"Class",criteria:"1"}, children:[{text:{name:"Q8",criteria:"1"}, children:[{text:{name:"1",criteria:"1"}, children:[]},{text:{name:"3",criteria:"2"}, children:[]}]},{text:{name:"3",criteria:"2"}, children:[]}]},{text:{name:"Q5",criteria:"2"}, children:[{text:{name:"2",criteria:"1"}, children:[]},{text:{name:"Q1",criteria:"2"}, children:[{text:{name:"Q4",criteria:"1"}, children:[{text:{name:"1",criteria:"1"}, children:[]},{text:{name:"3",criteria:"2"}, children:[]}]},{text:{name:"1",criteria:"2"}, children:[]}]}]}]},{text:{name:"Q1",criteria:"2"}, children:[{text:{name:"2",criteria:"1"}, children:[]},{text:{name:"1",criteria:"2"}, children:[]}]}]},{text:{name:"Difficulty",criteria:"2"}, children:[{text:{name:"Class",criteria:"1"}, children:[{text:{name:"3",criteria:"1"}, children:[]},{text:{name:"2",criteria:"2"}, children:[]}]},{text:{name:"Class",criteria:"2"}, children:[{text:{name:"Attendance",criteria:"1"}, children:[{text:{name:"1",criteria:"1"}, children:[]},{text:{name:"3",criteria:"2"}, children:[]}]},{text:{name:"3",criteria:"2"}, children:[]}]}]}]

	}
};

// // // // // // // // // // // // // // // // // // // // // // // // 

var config = {
	container: "#OrganiseChart-simple"
};

var parent_node = {
	text: { name: "Parent node" }
};

var first_child = {
	parent: parent_node,
	text: { name: "First child" }
};

var second_child = {
	parent: parent_node,
	text: { name: "Second child" }
};

//var simple_chart_config = [
//	config, parent_node,
//		first_child, second_child 
//];
