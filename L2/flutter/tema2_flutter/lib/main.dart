
// ignore_for_file: unnecessary_new, use_key_in_widget_constructors, no_logic_in_create_state, avoid_init_to_null, must_be_immutable, prefer_const_constructors, avoid_print, unused_local_variable

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:path/path.dart';
import 'package:tema2_flutter/model/Grade.dart';

void main() => runApp(new MyApp());

List<Grade> savedGlobal =[];

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    savedGlobal =[Grade(id: 1, teacherName: 'Linda Brown', studentName: 'Nancy Lopez', gradeValue: 1, subjectName: 'Drama', date: DateTime(2019)),
            Grade(id: 2, teacherName: 'John Smith', studentName: 'Sandra Wilson', gradeValue: 10, subjectName: 'Math', date: DateTime(2020,1,10)),
            Grade(id: 3, teacherName: 'Charles Davis', studentName: 'Sandra Wilson', gradeValue: 10, subjectName: 'Biology', date: DateTime(2020,1,10)),
            Grade(id: 4, teacherName: 'Linda Brown', studentName: 'Donna White', gradeValue: 10, subjectName: 'Drama', date: DateTime(2020,1,10)),
            Grade(id: 5, teacherName: 'Charles Davis', studentName: 'Lisa Lopez', gradeValue: 10, subjectName: 'Biology', date: DateTime(2020,1,10))] ; 
 

    return new MaterialApp(
      title: 'Virtual Grades',
      home: new MainPage(),
      color: Colors.grey,
    );
  }
}

class AddScreen extends StatefulWidget {
  
  Grade? grade=null;
  String action;  //can be 'update' or 'add'

  AddScreen(this.grade, this.action);

  @override
  // ignore: unnecessary_this
  AddScreenState createState() => new AddScreenState(this.grade, this.action);
}

class AddScreenState extends State<AddScreen> {
  
  Grade? grade;
  String action;

  AddScreenState(this.grade, this.action);

  @override
  Widget build(BuildContext context) {
    TextEditingController teacherCtr ;
    TextEditingController studentCtr;
    TextEditingController subjectCtr;
    TextEditingController gradeCtr;
    TextEditingController dateCtr ;
    if(grade == null){
      teacherCtr = TextEditingController(text: "");
      studentCtr = TextEditingController(text: "");
      subjectCtr = TextEditingController(text: "");
      gradeCtr = TextEditingController(text: "");
      dateCtr = TextEditingController(text: "");
    }
    else{
      teacherCtr = TextEditingController(text: grade!.teacherName);
      studentCtr = TextEditingController(text: grade!.studentName);
      subjectCtr = TextEditingController(text: grade!.subjectName);
      gradeCtr = TextEditingController(text: grade!.gradeValue.toString());
      dateCtr = TextEditingController(text: grade!.date.toString());
    }

    return Scaffold(
        resizeToAvoidBottomInset: false,
        backgroundColor: Colors.grey,
        body:  SafeArea(
          child:(new Container(
                  padding: new EdgeInsets.only(right: 13.0),
                  child: Column( crossAxisAlignment: CrossAxisAlignment.stretch,
                                 children: <Widget>[
                                 Column(
                                    mainAxisAlignment: MainAxisAlignment.center,
                                    crossAxisAlignment: CrossAxisAlignment.stretch,
                                    children: <Widget>[TextField(
                                        controller: teacherCtr,
                                        decoration:  InputDecoration(
                                          border: UnderlineInputBorder(),
                                          labelText: 'Teacher',
                                          labelStyle:  TextStyle(
                                              color: Colors.white,
                                            ), ),),
                                        ]),
                                  Padding(
                                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 16),
                                    child: TextField(
                                      controller: studentCtr,
                                        decoration:  InputDecoration(
                                          border: UnderlineInputBorder(),
                                          labelText: 'Student',
                                          labelStyle:  TextStyle(
                                              color: Colors.white,
                                            ),
                                    ),
                                  ),),
                                  Padding(
                                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 16),
                                    child: TextField(
                                      controller: subjectCtr,
                                        decoration:  InputDecoration(
                                          border: UnderlineInputBorder(),
                                          labelText: 'Subject',
                                          labelStyle:  TextStyle(
                                              color: Colors.white,
                                            ),
                                    ),
                                  ),),
                                  Padding(
                                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 16),
                                    child: TextField(
                                      controller: gradeCtr,
                                        decoration:  InputDecoration(
                                          border: UnderlineInputBorder(),
                                          labelText: 'Grade',
                                          labelStyle:  TextStyle(
                                              color: Colors.white,
                                            ),
                                    ),
                                  ),),
                                  Padding(
                                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 16),
                                    child: TextField(
                                      controller: dateCtr,
                                        decoration:  InputDecoration(
                                          border: UnderlineInputBorder(),
                                          labelText: 'Date',
                                          labelStyle:  TextStyle(
                                              color: Colors.white,
                                            ),

                                         ),
                                     
  
                                  ),),
                                  Padding(
                                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 16),
                                    child: TextButton(
                                           
                                          child: Text(action.toUpperCase(), style: TextStyle(fontSize: 20.0,color: Colors.red), ), 
                                           
                                          onPressed: () {
                                            Grade newGrade;
                                            String result= validateData(dateCtr.text, teacherCtr.text, studentCtr.text, subjectCtr.text,
                                                           gradeCtr.text);

                                            print(result);
                                            if(result==""){
                                              if(action == "update"){
                                                
                                                newGrade = Grade(date: DateTime.parse(dateCtr.text),
                                                    teacherName: teacherCtr.text, id: grade!.id, studentName: studentCtr.text,
                                                    subjectName: subjectCtr.text, gradeValue: int.parse(gradeCtr.text));
                                              }
                                              else{
                                                  newGrade = Grade(date: DateTime.parse(dateCtr.text),
                                                    teacherName: teacherCtr.text, id: -1, studentName: studentCtr.text,
                                                    subjectName: subjectCtr.text, gradeValue: int.parse(gradeCtr.text));
                                              }
                                              Navigator.pop(context, newGrade);
                                            }
                                            else{
                                              showDialog<String>(
                                                  context: context,
                                                  builder: (BuildContext context) => AlertDialog(
                                                      title: const Text('Alert!'),
                                                      content:  Text(result),
                                                      actions: <Widget>[
                                                        TextButton(
                                                          onPressed: () => Navigator.pop(context, 'Cancel'),
                                                          child: const Text('Cancel'),
                                                        )
                                                      ],
                                                    ),
                                                  );
                                            }
                                          },  )
                                        ),
            ]
          )))));
        }

  String validateData(String data, String teacher, String student, String subject, String grade) {

    String errors ="";
    if(teacher=="" || teacher == " "){
      errors+="Invalid teacher.\n";
    }
    if(subject=="" || subject == " "){
      errors+="Invalid subject.\n";
    }
    if(student=="" || student == " "){
      errors+="Invalid student.\n";
    }
    try{
      int gradeValue =int.parse(grade);
      if(gradeValue >10 || gradeValue <=0){
        errors+="Invalid grade.\n";
      }
    }
    on FormatException{
      errors+="Invalid grade; there are not only digits.\n";
    }

    try{
      final parsedData = DateTime.parse(data);
    }
    on FormatException{
        errors+="Invalid date.\n";
        print("date rr");
    }
    return errors;
  }
}
  

  
class MainPage extends StatefulWidget {


  @override
  _MainPageState createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
  final TextStyle _biggerFont = const TextStyle(fontSize: 18.0);

  int id = 6;
  @override
  Widget build(BuildContext context) {

    Iterable<ListTile> tiles = savedGlobal.map((Grade pair) {
      return new ListTile(
        
        onLongPress: () {
            showAlertDialog(BuildContext context) {
              Widget cancelButton = TextButton(
                child: Text("No" ,style: TextStyle(
                      color: Colors.white,
                    ),),
                
                onPressed:  () {
                   Navigator.of(context).pop();

                },
              );
              Widget continueButton = TextButton(
                child: Text("Yes" ,style: TextStyle(
                      color: Colors.white,
                    ),),
                onPressed:  () {
                   setState(() {
                    savedGlobal.remove(pair);
                    Navigator.of(context).pop();

           
                  });

                },
              );
              AlertDialog alert = AlertDialog(
                title: Text("Alert!"),
                backgroundColor: Colors.grey,
                
                content: Text("Deleting data?"),
                actions: [
                  cancelButton,
                  continueButton,
                ],
              );
              showDialog(
                context: context,
                builder: (BuildContext context) {
                  return alert;
                },
              );
}         
        showAlertDialog(context);
         
        },
        title: new Text(
          "Teacher: "+pair.teacherName+"\nStudent: "+pair.studentName+
          "\nSubject: "+pair.subjectName+"\nGrade: "+pair.gradeValue.toString()+
          "\nDate: "+pair.date.toString(),
          style: _biggerFont,
        ),
        trailing:   IconButton(
              icon: Icon(Icons.edit_outlined),
              color: Colors.red,
              onPressed: () async => {await Navigator.push( context,
                          MaterialPageRoute(
                          builder: (context) => AddScreen(pair, 'update'))).then((value) => {
                              setState(() {
                              if(value!=null){
                                savedGlobal[savedGlobal.indexWhere((element) => element.id == value.id)] = value;
                              }  })  })  }, 
      ));
    });

    final List<Widget> divided = ListTile.divideTiles(
      context: context,
      color: Colors.grey,
      tiles: tiles,
    ).toList();

    return new Scaffold(
      
      appBar: new AppBar(
        backgroundColor: Colors.grey,
        leading: GestureDetector(
            onTap: () async{ 
              await  Navigator.push(context,
              MaterialPageRoute( builder: (context) => AddScreen( null, 'add'))).then((value) => {
                setState(() { 
                  if(value!=null){
                    value.setId(id);
                    savedGlobal.add(value);
                    id++; 
                    print("id = "+id.toString());}})});                             
             },
            child: Icon(
              Icons.add,  
            )),
        title: const Text('Virtual Grades'),
      ),
      body: new ListView(children: divided ),
    );
  }


}