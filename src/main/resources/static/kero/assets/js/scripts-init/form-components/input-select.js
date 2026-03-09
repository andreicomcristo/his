// Forms Multi Select

$( document ).ready(function() {

    setTimeout(function () {

        $(".multiselect-dropdown").select2({
            theme: "bootstrap4",
            placeholder: "SELECIONE UMA OPÇÃO",
			allowClear: true
        });
		
		
		
		
		
		
		$(".multiselect-dropdown-permanece-aberto").select2({
		    theme: "bootstrap4",
		    placeholder: "SELECIONE UMA OPÇÃO",
		    allowClear: true,
		    closeOnSelect: false,
		    minimumResultsForSearch: 0,

		    matcher: function(params, data) {
		        if ($.trim(params.term) === '') return data;
		        let original = data.text.toLowerCase();
		        let term = params.term.toLowerCase();
		        return original.includes(term) ? data : null;
		    }
		}).on('select2:open select2:select', function () {
		    setTimeout(() => {
		        // Ajusta altura do dropdown sempre
		        const resultsList = document.querySelector('.select2-results__options');
		        if (resultsList) {
		            resultsList.style.maxHeight = '400px';
		            resultsList.style.overflowY = 'auto';
		        }

		        // Se houver uma opção selecionada, faz scroll até ela
		        let selected = $(this).find('option:selected').last();
		        if (selected.length) {
		            let id = selected.val();
		            const highlighted = document.querySelector('.select2-results__option[aria-selected][id*="' + id + '"]');
		            if (highlighted && resultsList) {
		                resultsList.scrollTop = highlighted.offsetTop - resultsList.offsetTop;
		            }
		        }
		    }, 0);
		});

		
		



		
		$('#example-single').multiselect({
            inheritClass: true
        });

        $('#example-multi').multiselect({
            inheritClass: true
        });

        $('#example-multi-check').multiselect({
            inheritClass: true
        });

    }, 2000);

});


