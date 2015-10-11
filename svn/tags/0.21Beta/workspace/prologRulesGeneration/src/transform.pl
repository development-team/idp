transform(Type, normDate) :- Type = date.
transform(Type, [calendar, normMonth]) :- Type = monthFull.
%transform(Type, normYear) :- Type = year.