Sub auto_che_table1()
'
' Macro1 Macro
' マクロ記録日 : 2006/3/3  ユーザー名 : 斎藤輪太郎
'

'
    Dim row As Integer
    Const COL = 1
    Dim hizuke As Date
    Dim wd As Integer
    Dim wdj As String
    Dim smonth As Integer
    Dim syear As Integer
        
    hizuke = InputBox("年月を入力して下さい(例：2006/4)")
    
    row = 2
    smonth = Month(hizuke)
    syear = Year(hizuke)
    
    Cells(1, 1) = hizuke
    Cells(1, 1).NumberFormatLocal = "yyyy""年""m""月"";@"
    
    Do While Month(hizuke) = smonth
        
    '変数「日付値」の値からWeekday関数を使って曜日を割り出す
        wd = Weekday(hizuke)

    '割り出された曜日を日本語に変換
        Select Case wd
            Case vbSunday
                wdj = "日"
            Case vbMonday
                wdj = "月"
            Case vbTuesday
                wdj = "火"
            Case vbWednesday
                wdj = "水"
            Case vbThursday
                wdj = "木"
            Case vbFriday
                wdj = "金"
            Case vbSaturday
                wdj = "土"
        End Select
        
        Cells(row, COL) = hizuke
        Cells(row, COL).NumberFormatLocal = "m""月""d""日"";@"
        Cells(row, COL + 1) = wdj
        
        If wdj = "土" Then
            Cells(row, COL + 1).Interior.ColorIndex = 34
        End If
        If wdj = "日" Then
            Cells(row, COL + 1).Interior.ColorIndex = 38
        End If
        hizuke = hizuke + 1
        row = row + 1
        
    Loop

    Range(Cells(1, 1), Cells(row - 1, 5)).Select
    Selection.Borders(xlDiagonalDown).LineStyle = xlNone
    Selection.Borders(xlDiagonalUp).LineStyle = xlNone
    With Selection.Borders(xlEdgeLeft)
        .LineStyle = xlContinuous
        .Weight = xlThin
        .ColorIndex = xlAutomatic
    End With
    With Selection.Borders(xlEdgeTop)
        .LineStyle = xlContinuous
        .Weight = xlThin
        .ColorIndex = xlAutomatic
    End With
    With Selection.Borders(xlEdgeBottom)
        .LineStyle = xlContinuous
        .Weight = xlThin
        .ColorIndex = xlAutomatic
    End With
    With Selection.Borders(xlEdgeRight)
        .LineStyle = xlContinuous
        .Weight = xlThin
        .ColorIndex = xlAutomatic
    End With
    With Selection.Borders(xlInsideVertical)
        .LineStyle = xlContinuous
        .Weight = xlThin
        .ColorIndex = xlAutomatic
    End With
    With Selection.Borders(xlInsideHorizontal)
        .LineStyle = xlContinuous
        .Weight = xlThin
        .ColorIndex = xlAutomatic
    End With
    Selection.Borders(xlDiagonalDown).LineStyle = xlNone
    Selection.Borders(xlDiagonalUp).LineStyle = xlNone
    With Selection.Borders(xlEdgeLeft)
        .LineStyle = xlContinuous
        .Weight = xlMedium
        .ColorIndex = xlAutomatic
    End With
    With Selection.Borders(xlEdgeTop)
        .LineStyle = xlContinuous
        .Weight = xlMedium
        .ColorIndex = xlAutomatic
    End With
    With Selection.Borders(xlEdgeBottom)
        .LineStyle = xlContinuous
        .Weight = xlMedium
        .ColorIndex = xlAutomatic
    End With
    With Selection.Borders(xlEdgeRight)
        .LineStyle = xlContinuous
        .Weight = xlMedium
        .ColorIndex = xlAutomatic
    End With
    With Selection.Borders(xlInsideVertical)
        .LineStyle = xlContinuous
        .Weight = xlThin
        .ColorIndex = xlAutomatic
    End With
    With Selection.Borders(xlInsideHorizontal)
        .LineStyle = xlContinuous
        .Weight = xlThin
        .ColorIndex = xlAutomatic
    End With
    
    Range("A1:E1").Select
    Selection.Borders(xlDiagonalDown).LineStyle = xlNone
    Selection.Borders(xlDiagonalUp).LineStyle = xlNone
    With Selection.Borders(xlEdgeLeft)
        .LineStyle = xlContinuous
        .Weight = xlMedium
        .ColorIndex = xlAutomatic
    End With
    With Selection.Borders(xlEdgeTop)
        .LineStyle = xlContinuous
        .Weight = xlMedium
        .ColorIndex = xlAutomatic
    End With
    With Selection.Borders(xlEdgeBottom)
        .LineStyle = xlContinuous
        .Weight = xlMedium
        .ColorIndex = xlAutomatic
    End With
    With Selection.Borders(xlEdgeRight)
        .LineStyle = xlContinuous
        .Weight = xlMedium
        .ColorIndex = xlAutomatic
    End With
    With Selection.Borders(xlInsideVertical)
        .LineStyle = xlContinuous
        .Weight = xlThin
        .ColorIndex = xlAutomatic
    End With
    Columns("A:A").Select
    Selection.ColumnWidth = 13
    Columns("B:B").Select
    Selection.ColumnWidth = 3
    Columns("C:C").Select
    Selection.ColumnWidth = 60
    Columns("D:D").Select
    Selection.ColumnWidth = 10
    Columns("E:E").Select
    Selection.ColumnWidth = 30
    Range("C1").Select
    ActiveCell.FormulaR1C1 = "予定"
    ActiveCell.Characters(1, 2).PhoneticCharacters = "ヨテイ"
    Range("D1").Select
    ActiveCell.FormulaR1C1 = "滞在場所"
    ActiveCell.Characters(1, 2).PhoneticCharacters = "タイザイバショ"
    Range("E1").Select
    ActiveCell.FormulaR1C1 = "備考"
    ActiveCell.Characters(1, 2).PhoneticCharacters = "ビコウ"
    
    Range("C1:E1").Select
    With Selection
        .HorizontalAlignment = xlCenter
        .VerticalAlignment = xlCenter
        .WrapText = False
        .Orientation = 0
        .AddIndent = False
        .IndentLevel = 0
        .ShrinkToFit = False
        .ReadingOrder = xlContext
        .MergeCells = False
    End With
    Range("A1").Select
    With Selection.Font
        .Name = "ＭＳ Ｐゴシック"
        .Size = 12
        .Strikethrough = False
        .Superscript = False
        .Subscript = False
        .OutlineFont = False
        .Shadow = False
        .Underline = xlUnderlineStyleNone
        .ColorIndex = xlAutomatic
    End With
    Selection.Font.Bold = True
    
End Sub
