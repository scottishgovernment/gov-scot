/**
 * Publication types component
 */

define([], function () {
    'use strict';

    var publicationTypes = [
      {
        name: 'consultation_paper',
        aps: 'Consultation Paper'
      },
      {
        name: 'consultation_response',
        aps: 'Consultation Responses'
      },
      {
        name: 'corporate_report',
        aps: 'Report',
        non_aps: 'corporate_report'
      },
      {
        name: 'foi',
        non_aps: 'foi'
      },
      {
        name: 'transparency_data',
        non_aps: 'transparency_data'
      },
      {
        name: 'correspondence',
        non_aps: 'correspondence',
        aps: 'Newsletter'
      },
      {
        name: 'advice_guidance',
        aps: 'Guidance',
        non_aps: 'advice_guidance'
      },
      {
        name: 'agreement',
        non_aps: 'agreement'
      },
      {
        name: 'legislation',
        aps: 'Legislation'
      },
      {
        name: 'legislative_consent_motion',
        non_aps: 'legislative_consent_motion'
      },
      {
        name: 'minutes',
        non_aps: 'minutes'
      },
      {
        name: 'research_finding',
        aps: 'Research Finding'
      },
      {
        name: 'research_publication',
        aps: 'Research Publication'
      },
      {
        name: 'Statistics dataset',
        aps: 'Statistics dataset'
      },
      {
        name: 'statistics_publication',
        aps: 'Statistics Publication'
      },
      {
        name: 'statistics_dataset',
        aps: 'Statistics Dataset'
      },
      {
        name: 'map',
        non_aps: 'map'
      },
      {
        name: 'form',
        non_aps: 'form'
      },
      {
        name: 'factsheet',
        non_aps: 'case_study_etc'
      },
      {
        name: 'speech_or_statement',
        non_aps: 'speech_or_statement'
      },
      {
        name: 'publication',
        aps: 'Publication'
      }
    ];

    function isAps(type) {
      return type.aps !== undefined;
    }

    function isNonAps(type) {
      return type.non_aps !== undefined;
    }

    function apsPublicationType (type) {
      return type.aps;
    }

    function nonapsPublicationType (type) {
      return type.non_aps;
    }

    function allApsPublicationTypes() {
        return publicationTypes.filter(isAps).map(apsPublicationType);
    }

    function allNonApsPublicationTypes() {
        return publicationTypes.filter(isNonAps).map(nonapsPublicationType);
    }

    return {
        allApsPublicationTypes: allApsPublicationTypes,
        allNonApsPublicationTypes: allNonApsPublicationTypes,

        formatsToInclude: function(types, typesParamLookup) {
            if (!types) {
                return allNonApsPublicationTypes();
            }

            var formats = publicationTypes
                .filter(isNonAps)
                .filter(function (type) { return typesParamLookup[type.name];})
                .map(nonapsPublicationType);
            return formats;
        },

        apsPublicationTypes: function(types, typesParamLookup) {
          if (!types) {
            return allApsPublicationTypes();
          }

          return publicationTypes
            .filter(isAps)
            .filter(function (type) { return typesParamLookup[type.name];})
            .map(apsPublicationType);
        }
    };
});
