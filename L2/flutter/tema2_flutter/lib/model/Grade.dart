// ignore_for_file: file_names

class Grade {
  int id;
  final String teacherName;
  final String studentName;
  final String subjectName;
  final int gradeValue;
  final DateTime date;

  Grade({
    required this.id,
    required this.teacherName,
    required this.studentName,
    required this.gradeValue,
    required this.subjectName,
    required this.date,


  });

  setId(int id){
    this.id  = id;
  }

 
}
